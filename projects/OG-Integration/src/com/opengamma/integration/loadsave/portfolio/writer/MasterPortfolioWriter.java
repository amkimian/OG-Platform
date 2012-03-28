/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.integration.loadsave.portfolio.writer;

import javax.time.calendar.ZonedDateTime;

import org.apache.commons.lang.ArrayUtils;

import com.opengamma.id.ExternalIdSearch;
import com.opengamma.id.ExternalIdSearchType;
import com.opengamma.id.VersionCorrection;
import com.opengamma.master.portfolio.ManageablePortfolio;
import com.opengamma.master.portfolio.ManageablePortfolioNode;
import com.opengamma.master.portfolio.PortfolioDocument;
import com.opengamma.master.portfolio.PortfolioMaster;
import com.opengamma.master.portfolio.PortfolioSearchRequest;
import com.opengamma.master.portfolio.PortfolioSearchResult;
import com.opengamma.master.position.ManageablePosition;
import com.opengamma.master.position.PositionDocument;
import com.opengamma.master.position.PositionMaster;
import com.opengamma.master.position.PositionSearchRequest;
import com.opengamma.master.position.PositionSearchResult;
import com.opengamma.master.security.ManageableSecurity;
import com.opengamma.master.security.SecurityDocument;
import com.opengamma.master.security.SecurityMaster;
import com.opengamma.master.security.SecuritySearchRequest;
import com.opengamma.master.security.SecuritySearchResult;
import com.opengamma.master.security.SecuritySearchSortOrder;
import com.opengamma.util.ArgumentChecker;

/**
 * A class that facilitates writing securities and portfolio positions and trades
 */
public class MasterPortfolioWriter implements PortfolioWriter {

  private final PortfolioMaster _portfolioMaster;
  private final PositionMaster _positionMaster;
  private final SecurityMaster _securityMaster;
  
  private PortfolioDocument _portfolioDocument;
  private ManageablePortfolioNode _currentNode;
  private ManageablePortfolioNode _originalNode;
  private ManageablePortfolioNode _originalRoot;
  
    
  public MasterPortfolioWriter(String portfolioName, PortfolioMaster portfolioMaster, 
      PositionMaster positionMaster, SecurityMaster securityMaster) {

    ArgumentChecker.notEmpty(portfolioName, "portfolioName");
    ArgumentChecker.notNull(portfolioMaster, "portfolioMaster");
    ArgumentChecker.notNull(positionMaster, "positionMaster");
    ArgumentChecker.notNull(securityMaster, "securityMaster");
    
    _portfolioMaster = portfolioMaster;
    _positionMaster = positionMaster;
    _securityMaster = securityMaster;
    _portfolioDocument = createPortfolio(portfolioName);

  }

  /*
   * writeSecurity searches for an existing security that matches an external id search, and attempts to
   * reuse/update it wherever possible, instead of creating a new one.
   */
  @Override
  public ManageableSecurity writeSecurity(ManageableSecurity security) {
    
    ArgumentChecker.notNull(security, "security");
    
    SecuritySearchRequest searchReq = new SecuritySearchRequest();
    ExternalIdSearch idSearch = new ExternalIdSearch(security.getExternalIdBundle());  // match any one of the IDs
    searchReq.setVersionCorrection(VersionCorrection.ofVersionAsOf(ZonedDateTime.now())); // valid now
    searchReq.setExternalIdSearch(idSearch);
    searchReq.setFullDetail(true);
    searchReq.setSortOrder(SecuritySearchSortOrder.VERSION_FROM_INSTANT_DESC);
    SecuritySearchResult searchResult = _securityMaster.search(searchReq);
    for (ManageableSecurity foundSecurity : searchResult.getSecurities()) {  
      if (weakEquals(foundSecurity, security)) {
        // It's already there, don't update or add it
        return foundSecurity;
      }
    }
    // Not found, so add it
    SecurityDocument addDoc = new SecurityDocument(security);
    SecurityDocument result = _securityMaster.add(addDoc);
    return result.getSecurity();
  }
  
  // This weak equals does not actually compare the security's fields, just the type, external ids and attributes :(
  private boolean weakEquals(ManageableSecurity sec1, ManageableSecurity sec2) {
    return sec1.getName().equals(sec2.getName()) &&
           sec1.getSecurityType().equals(sec2.getSecurityType()) &&
           sec1.getExternalIdBundle().equals(sec2.getExternalIdBundle()) &&
           sec1.getAttributes().equals(sec2.getAttributes());
  }
  
  
  /*
   * WritePosition checks if the position exists in the previous version of the portfolio.
   * If so, the existing position is reused.
   */
  @Override
  public ManageablePosition writePosition(ManageablePosition position) {
    
    ArgumentChecker.notNull(position, "position");
    
    ManageablePosition existingPosition = null;
    
    if (!(_originalNode == null) && !_originalNode.getPositionIds().isEmpty()) {
      PositionSearchRequest searchReq = new PositionSearchRequest();
      
      // Filter positions in current node of original portfolio
      searchReq.setPositionObjectIds(_originalNode.getPositionIds());

      // Filter positions with same external ids
      ExternalIdSearch externalIdSearch = new ExternalIdSearch();
      externalIdSearch.addExternalIds(position.getSecurityLink().getExternalIds()); 
      externalIdSearch.setSearchType(ExternalIdSearchType.ALL);
      searchReq.setSecurityIdSearch(externalIdSearch);
      
      // Filter positions with the same quantity
      searchReq.setMinQuantity(position.getQuantity());
      searchReq.setMaxQuantity(position.getQuantity());

      // Search
      PositionSearchResult searchResult = _positionMaster.search(searchReq);
      
      // Get the first match if found
      PositionDocument firstDocument = searchResult.getFirstDocument();
      if (firstDocument != null) {        
        existingPosition = firstDocument.getPosition();
      }
      
      // TODO also confirm that all the associated trades are identical
    }
 
    if (existingPosition == null) {
      // Add the new position to the position master
      PositionDocument addedDoc = _positionMaster.add(new PositionDocument(position));

      // Add the new position to the portfolio
      _currentNode.addPosition(addedDoc.getUniqueId());
      
      // Return the new position
      return addedDoc.getPosition();
      
    } else {
      // Add the existing position to the portfolio
      _currentNode.addPosition(existingPosition.getUniqueId());
      
      // Return the existing position
      return existingPosition;
    }
  }
  
  public ManageablePortfolioNode getCurrentNode() {
    return _currentNode;
  }
  
  public ManageablePortfolioNode setCurrentNode(ManageablePortfolioNode node) {
    
    ArgumentChecker.notNull(node, "node");
    
    // Attempt to find equivalent node in earlier version of portfolio
    if (_originalRoot != null) {
      _originalNode = _originalRoot.findNodeByName(node.getName());
    }
    _currentNode = node;
    return _currentNode;
  }


  @Override
  public String[] getCurrentPath() {
    return null; // TODO FIXXXXXXXXXXXXXXXXXXXXXXXXXXX
  }

  @Override
  public void setPath(String[] newPath) {
    
    ArgumentChecker.notNull(newPath, "newPath");
    
    if (newPath.length == 0) {
      return;
    }
    if (_originalRoot != null) {
      _originalNode = findNode(newPath, _originalRoot);
    }
    _currentNode = createNode(newPath, _portfolioDocument.getPortfolio().getRootNode());
  }

  @Override
  public void flush() {
    _portfolioDocument = _portfolioMaster.update(_portfolioDocument);
  }
  
  @Override
  public void close() {
    flush();
  }
  
  private ManageablePortfolioNode findNode(String[] path, ManageablePortfolioNode startNode) {
    
    // Degenerate case
    if (path.length == 1) {
      if (startNode.name().equals(path[0])) {
        return startNode;
      } else {
        return null;
      }
    
    // Recursive case, traverse all child nodes
    } else {
      for (ManageablePortfolioNode childNode : startNode.getChildNodes()) {
        String[] newPath = (String[]) ArrayUtils.subarray(path, 1, path.length - 1);
        ManageablePortfolioNode result = findNode(newPath, childNode);
        if (result != null) {
          return result;
        }
      }
      return null;
    }
  }
  
  private ManageablePortfolioNode createNode(String[] path, ManageablePortfolioNode startNode) {
    ManageablePortfolioNode node = startNode;
    
    for (String p : path) {
      for (ManageablePortfolioNode n : node.getChildNodes()) {
        if (n.getName().equals(p)) {
          node = n;
          break;
        }
        ManageablePortfolioNode newNode = new ManageablePortfolioNode(p);
        node.addChildNode(newNode);
        node = newNode;
      }
    }
    return node;
  }
  
  private PortfolioDocument createPortfolio(String portfolioName) {

    // Create a new root node
    ManageablePortfolioNode rootNode = new ManageablePortfolioNode(portfolioName);

    // Check to see whether the portfolio already exists
    PortfolioSearchRequest portSearchRequest = new PortfolioSearchRequest();
    portSearchRequest.setName(portfolioName);
    PortfolioSearchResult portSearchResult = _portfolioMaster.search(portSearchRequest);
    PortfolioDocument portfolioDoc = portSearchResult.getFirstDocument();

    // If it doesn't, create it (add) 
    if (portfolioDoc == null) {
      ManageablePortfolio portfolio = new ManageablePortfolio(portfolioName, rootNode);
      portfolioDoc = new PortfolioDocument();
      portfolioDoc.setPortfolio(portfolio);
      portfolioDoc = _portfolioMaster.add(portfolioDoc);
      _originalRoot = null;
      _originalNode = null;
      
    // If it does, create a new version of the existing portfolio (update) with a new root node
    } else {
      ManageablePortfolio portfolio = portfolioDoc.getPortfolio();
      _originalRoot = portfolio.getRootNode();
      _originalNode = _originalRoot;
      portfolio.setRootNode(rootNode);
      portfolioDoc.setPortfolio(portfolio);
      portfolioDoc = _portfolioMaster.update(portfolioDoc);
    }
    
    // Set current node to the root node
    _currentNode = portfolioDoc.getPortfolio().getRootNode();
    
    return portfolioDoc;
  }

}
