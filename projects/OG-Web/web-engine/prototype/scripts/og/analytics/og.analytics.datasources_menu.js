/*
 * Copyright 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * Please see distribution for license.
 */
$.register_module({
    name: 'og.analytics.DatasourcesMenu',
    dependencies: ['og.analytics.DropMenu'],
    obj: function () {
        return function (config) {
            if (!config) return;

            // Private
            var menu = new og.analytics.DropMenu({
                    $cntr: config.cntr,
                    data: ['Live', 'Snapshot', 'Historical'],
                    tmpl: config.tmpl
                }),
                query = [], resolver_keys = [], snapshots = {}, $dom, $query, $option, $snapshot_opts, $historical_opts,
                default_type_txt = 'select type...', default_sel_txt = 'select data source...',
                del_s = '.og-icon-delete', parent_s = '.OG-dropmenu-options', wrapper = '<wrapper>', type_s = '.type',
                source_s = '.source',  extra_opts_s = '.extra-opts', latest_s = '.latest', custom_s = '.custom',
                custom_val = 'Custom', date_selected_s = 'date-selected', active_s = 'active', versions_s = '.versions',
                corrections_s = '.corrections', events = {
                    sourcespopulated: 'dropmenu:ds:sourcespopulated',
                    typereset: 'dropmenu:ds:typereset',
                    typeselected:'dropmenu:ds:typesselected',
                    dataselected: 'dropmenu:ds:dataselected',
                    optsrepositioned: 'dropmenu:ds:optsrespositioned',
                    resetdsquery:'dropmenu:resetquery',
                    queryresequested:'dropmenu:queryresequested'
                };
            var date_handler = function (entry) { // TODO AG: refocus custom, hide datepicker
                if (!~entry || !menu.opts[entry]) return;
                var custom = $(custom_s, menu.opts[entry]), latest = $(latest_s, menu.opts[entry]),
                    idx = query.pluck('pos').indexOf(menu.opts[entry].data('pos'));
                if (custom) custom.addClass(active_s+ ' ' +date_selected_s);
                if (latest) latest.removeClass(active_s);
                if (custom.parent().is(versions_s)) query[idx].version_date = custom.datepicker('getDate');
                else if (custom.parent().is(corrections_s)) query[idx].correction_date = custom.val();
                else query[idx].date = custom.val();
            };
            var delete_handler = function (entry) {
                if (!~entry || !menu.opts[entry]) return;
                if (menu.opts.length === 1 && query.length) return remove_ext_opts(entry), reset_query(entry);
                var idx = query.pluck('pos').indexOf(menu.opts[entry].data('pos')),
                    sel_pos = menu.opts[entry].data('pos');
                menu.delete_handler(menu.opts[entry]);
                if (menu.opts.length) {
                    for (var i = ~idx ? idx : sel_pos, len = query.length; i < len; query[i++].pos -= 1);
                    if (~idx) return remove_entry(idx), display_query(); // emitEvent; optsrepositioned
                }
            };
            var display_datepicker = function (entry) {
                if (!~entry || !menu.opts[entry]) return;
                $(custom_s, menu.opts[entry])
                    .datepicker({onSelect: function() {date_handler(entry);}, dateFormat:'yy-mm-dd'})
                    .datepicker('show');
            };
            var display_query = function () {
                if(query.length) {
                    var i = 0, arr = [];
                    query.sort(menu.sort_opts).forEach(function (entry) { // revisit the need for sorting this..
                        if (i > 0) arr[i++] = $dom.toggle_infix.html() + " ";
                        arr[i++] = entry;
                    });
                    $query.html(arr.reduce(function (a, v) {
                        return a += v.type ? menu.capitalize(v.type) + ":" + v.src : menu.capitalize(v);
                    }, ''));
                } else $query.text(default_sel_txt);
            };
            var enable_extra_options = function (entry, val) {
                if (!~entry || !menu.opts[entry]) return;
                var inputs = $(extra_opts_s, menu.opts[entry]).find('input');
                if (!inputs) return;
                if (val) inputs.removeAttr('disabled').filter(latest_s).addClass(active_s);
                else inputs.attr('disabled', true).filter('.'+active_s).removeClass(active_s);
                inputs.filter(custom_s).removeClass(active_s+ ' ' +date_selected_s).val(custom_val);
            };
            var get_snapshot = function (id) {
                if (!id) return;
                return Object.keys(snapshots).filter(function(key) {
                    return snapshots[key] === id;
                })[0];
            };
            var init = function (config) {
                if (!config) return;
                menu.addListener(events.resetquery, menu.reset_query);
                $dom = menu.$dom;
                if ($dom) {
                    $query = $('.datasources-query', $dom.toggle);
                    $option = $(wrapper).append('<option>');
                    $.when(
                        og.api.text({module: 'og.analytics.form_datasources_snapshot_opts_tash'}),
                        og.api.text({module: 'og.analytics.form_datasources_historical_opts_tash'}),
                        og.api.rest.configs.get({type: 'HistoricalTimeSeriesRating'})
                    ).then(function(snapshot, historical, res_keys){
                        if (!snapshot.error) $snapshot_opts = $(wrapper).append(snapshot);
                        if (!historical.error) $historical_opts = $(wrapper).append(historical);
                        if (!res_keys.error && res_keys.data && res_keys.data.data)
                            res_keys.data.data.forEach(function (entry) {resolver_keys.push(entry.split('|')[1]);});
                        if ($dom.menu) {
                            $dom.menu.on('click', 'input, button, div.og-icon-delete, a.OG-link-add', menu_handler)
                                .on('change', 'select', menu_handler);
                            init_listeners();
                        }
                        if (config.opts) menu.replay_query(config.opts);
                    });
                }
            };
            var init_listeners = function () {
                menu.addListener(events.queryresequested, remove_orphans);
            };
            var menu_handler = function (event) { // TODO AG: Refactor
                var entry, sel_pos, elem = $(event.srcElement || event.target), parent = elem.parents(parent_s);
                if (!parent) return;
                entry = parent.data('pos');
                if (elem.is(menu.$dom.add)) return menu.stop(event), menu.add_handler();
                if (elem.is(del_s)) return menu.stop(event), delete_handler(entry);
                if (elem.is(type_s)) return type_handler(entry);
                if (elem.is(source_s)) return source_handler(entry);
                if (elem.is(custom_s)) return display_datepicker(entry);
                if (elem.is(latest_s)) return remove_date(entry);
                if (elem.is('button')) return menu.button_handler(elem.text());
            };
            var populate_historical = function (entry) {
                if (!~entry || !menu.opts[entry]) return;
                var source_select = $(source_s, menu.opts[entry]);
                if (resolver_keys && source_select) {
                    populate_src_options(entry, resolver_keys);
                    source_select.after($historical_opts.html());
                }
            };
            var populate_livedatasources = function (entry, config) {
                if (!~entry || !menu.opts[entry]) return;
                og.api.rest.livedatasources.get().pipe(function (resp) {
                    if (resp.error) return;
                    if (config && config.preHandler) config.preHandler();
                    if (resp.data) populate_src_options(entry, resp.data);
                    if (config && config.postHandler) config.postHandler();
                });
            };
            var populate_marketdatasnapshots = function (entry) {
                if (!~entry || !menu.opts[entry]) return;
                og.api.rest.marketdatasnapshots.get().pipe(function (resp) {
                    if (resp.error) return;
                    if (resp.data && resp.data[0]) populate_src_options(entry, resp.data[0].snapshots);
                });
                /*.pipe(function () {
                    $source_select.after($snapshot_opts.html());
                });*/
            };
            var populate_src_options = function (entry, data) {
                if (!~entry || !menu.opts[entry]) return;
                var source_select = $(source_s, menu.opts[entry]),
                    type_val = $(type_s, menu.opts[entry]).val().toLowerCase();
                if (type_val) menu.opts[entry].data('type', type_val).addClass(type_val);
                if (source_select) data.forEach(function (d) {
                    if (d.name) snapshots[d.name] = d.id;
                    source_select.append($($option.html()).text(d.name || d));
                });
            };
            var remove_date = function (entry) {
                if (!~entry || !menu.opts[entry]) return;
                var custom = $(custom_s, menu.opts[entry]).removeClass(active_s+ ' ' +date_selected_s).val(custom_val),
                    latest = $(latest_s, menu.opts[entry]).addClass(active_s);
                if (custom.parent().is(versions_s)) delete query[entry].version_date;
                else if (custom.parent().is(corrections_s)) delete query[entry].correction_date;
                else delete query[entry].date;
            };
            var remove_entry = function (entry) {
                if (menu.opts.length === 1 && query.length === 1) return query.length = 0; // emitEvent; resetquery
                if (~entry) query.splice(entry, 1);
            };
            var remove_ext_opts = function (entry) {
                if (!~entry || !menu.opts[entry]) return;
                var parent = menu.opts[entry];
                return reset_source_select(entry), parent.removeClass(parent.data('type')).find(extra_opts_s).remove();
            };
            var remove_orphans = function () {
                for (var i = menu.opts.length - 1; 0 < i; i-=1){
                    if (menu.opts.length === 1) break;
                    var option = menu.opts[i];
                    if ($(type_s, option).val() === default_type_txt || $(source_s, option).val() === default_sel_txt)
                        menu.delete_handler(option);
                }
            };
            var reset_query = function (entry) {
                if (!~entry || !menu.opts[entry]) return;
                var type_select = $(type_s, menu.opts[entry]);
                return $query.text(default_sel_txt), type_select.val(default_sel_txt).focus(), remove_entry();
            };
            var replay_val = function (entry, data) {
                if ((!~entry || !data) && !menu.opts[entry]) return;
                var type_select = $(type_s, menu.opts[entry]), source_select = $(source_s, menu.opts[entry]),
                    source = data.snapshotId ? get_snapshot(data.snapshotId) : data.source,
                    type = menu.capitalize(data.marketDataType);
                switch (data.marketDataType) {
                    case 'live' :
                    case 'snapshot':
                        type_select.val(type); source_select.val(source); break;
                    case 'latestHistorical':
                    case 'fixedHistorical':
                        break;
                }
            };
            var reset_source_select = function (entry) {
                if (!~entry || !menu.opts[entry]) return;
                var parent = $(source_s, menu.opts[entry]).parent(), source_select = $(source_s, parent).remove();
                if (!parent && !source_select) return;
                // IE doesn't seem to update Select on the fly, so, take it out of DOM, update childNodes, append
                // it back to the parent
                source_select.empty().append($($option.html()).text(default_sel_txt));
                parent.append(source_select);
            };
            var splice_replay_vals = function (entry, data) {
                if (!~entry || !data) return;
                var source = data.snapshotId ? get_snapshot(data.snapshotId) : data.source,
                    type = menu.capitalize(data.marketDataType);
                query.splice(entry, 0, {pos: entry, src: source, type: type});
            };
            var source_handler = function (entry) {
                if (!~entry || !menu.opts[entry]) return;
                var sel_pos = menu.opts[entry].data('pos'),
                    type_val = $(type_s, menu.opts[entry]).val().toLowerCase(),
                    source_select = $(source_s, menu.opts[entry]),
                    source_val = source_select.val(),
                    idx = query.pluck('pos').indexOf(sel_pos);
                if (source_val === default_sel_txt) {
                    return remove_entry(idx), display_query(), enable_extra_options(entry, false);
                } else if (~idx) query[idx] = {pos:sel_pos, type:type_val, src:source_val};
                else query.splice(sel_pos, 0, {pos:sel_pos, type:type_val, src:source_val});
                enable_extra_options(entry, true);
                display_query();
                // emitEvent; dataselected
            };
            var type_handler = function (entry) {
                if (!~entry || !menu.opts[entry]) return;
                var parent = menu.opts[entry], type_select = $(type_s, parent),
                    type_val = type_select.val().toLowerCase(), idx = query.pluck('pos').indexOf(parent.data('pos'));
                if (type_val === default_type_txt.toLowerCase()){
                    if (menu.opts.length === 1 && query.length === 1) return remove_ext_opts(entry), reset_query(entry);
                    return remove_entry(idx), remove_ext_opts(entry), display_query(),
                        enable_extra_options(entry, false);
                    // emitEvent; typeselected
                }
                if (parent.hasClass(parent.data('type'))) {
                    remove_entry(idx); remove_ext_opts(entry); display_query();
                }
                switch (type_val) {
                    case 'live': populate_livedatasources(entry); break;
                    case 'snapshot': populate_marketdatasnapshots(entry); break;
                    case 'historical': populate_historical(entry); break;
                    //no default
                }
            };

            // Public
            menu.destroy = function () {};
            menu.get_query = function () {
                if (!query.length) return;
                var arr = [];
                query.forEach(function (entry) {
                    var obj = {}, val = entry.type.toLowerCase();
                    switch (val) {
                        case 'live': obj['marketDataType'] = val, obj['source'] = entry.src; break;
                        case 'snapshot': obj['marketDataType'] = val, obj['snapshotId'] = snapshots[entry.src]; break;
                        case 'historical':
                            if (entry.date) {
                                obj['marketDataType'] = 'fixedHistorical';
                                obj['date'] = entry.date;
                            } else obj['marketDataType'] = 'latestHistorical';
                            obj['resolverKey'] = entry.src; break;
                        //no default
                    }
                    arr.push(obj);
                });
                menu.emitEvent(events.queryresequested);
                return arr;
            };
            menu.replay_query = function (conf) {
                console.log(conf);
                if (!conf && !conf.datasources || !$.isArray(conf.datasources)) return;
                menu.opts.forEach(function (option) {
                    option.remove();
                });
                menu.opts.length = 0;
                query = [];
                conf.datasources.forEach(function (src, index) {
                    if (menu.opts.length < conf.datasources.length) menu.add_handler();
                    switch (src.marketDataType) {
                        case 'live':
                            return populate_livedatasources(index,{
                                postHandler: (function (index, src) {
                                    return function () {
                                        splice_replay_vals(index, src);
                                        replay_val(index, src);
                                        display_query();
                                    }
                                })(index, src)
                            });
                        /*case 'snapshot':
                            og.api.rest.marketdatasnapshots.get().pipe(function (resp) {
                                if (resp.error) return;
                                populate_src_options(resp.data[0].snapshots);
                            }).pipe(function () {
                                set_select_vals(src, index);
                            });
                            break;
                        case 'latestHistorical':
                        case 'fixedHistorical' :
                            populate_historical();
                            enable_extra_options(true);
                            query.splice(index, 0, {pos: index, src: src.resolverKey, type: 'Historical'});
                            if (src.date) {
                                $custom.addClass(active_s+ ' ' +date_selected_s);
                                $latest.removeClass(active_s);
                                $custom.val(src.date);
                                query[index].date = src.date;
                            }
                            display_query();
                            break;

                        //no default*/
                    }
                });
            };
            menu.reset_query = function () {
                for (var i = menu.opts.length - 1; 0 < i; i-=1) {
                    if (menu.opts.length === 1) {
                        menu.opts[i].val(default_sel_txt);
                        break;
                    }
                    delete_handler(i);
                }
                return remove_ext_opts(), reset_query();
            };
            return init(config), menu;
        };
    }
});