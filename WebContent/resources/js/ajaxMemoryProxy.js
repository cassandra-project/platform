Ext.define('Ext.ux.data.irs.AjaxMemoryProxy', {
       extend: 'Ext.data.proxy.Ajax',
       alias: 'proxy.ajaxmemory',
       alternateClassName: 'Ext.irs.AjaxMemoryProxy',
       actionMethods: {
        create : 'POST',
        read   : 'GET',
        update : 'PUT',
        destroy: 'DELETE'
    },
       read : function(operation, callback, scope){
              // need to have this one atleast once;
              if(this.proxyData==undefined) {
                     return this.doRequest.apply(this, arguments);
              }
              var reader = this.getReader(),
              result = reader.read(this.proxyData),
              sorters, filters, sorterFn, records;

              scope = scope || this;

              // filtering
              filters = operation.filters;
              if (filters.length > 0) {
                     //at this point we have an array of  Ext.util.Filter objects to filter with,
                     //so here we construct a function that combines these filters by ANDing them together
                     records = [];

                     Ext.each(result.records, function(record) {
                           var isMatch = true,
                           length = filters.length,
                           i;

                           for (i = 0; i < length; i++) {
                                  var filter = filters[i],
                                  fn     = filter.filterFn,
                                  scope  = filter.scope;

                                  isMatch = isMatch && fn.call(scope, record);
                           }
                           if (isMatch) {
                                  records.push(record);
                           }
                     }, this);

                     result.records = records;
                     result.totalRecords = result.total = records.length;
              }

              // sorting
              sorters = operation.sorters;
              if (sorters.length > 0) {
                     //construct an amalgamated sorter function which combines all of the Sorters passed
                     sorterFn = function(r1, r2) {
                           var result = sorters[0].sort(r1, r2),
                           length = sorters.length,
                           i;

                           //if we have more than one sorter, OR any additional sorter functions together
                           for (i = 1; i < length; i++) {
                                  result = result || sorters[i].sort.call(this, r1, r2);
                           }               

                           return result;
                     };

                     result.records.sort(sorterFn);
              }

              // paging (use undefined cause start can also be 0 (thus false))
              if (operation.start !== undefined && operation.limit !== undefined) {
                     result.records = result.records.slice(operation.start, operation.start + operation.limit);
                     result.count = result.records.length;
              }

              Ext.apply(operation, {
                     resultSet: result
              });

              operation.setCompleted();
              operation.setSuccessful();

              Ext.Function.defer(function () {
                     Ext.callback(callback, scope, [operation]);
              }, 10);
       },
       doRequest: function(operation, callback, scope) {

              var writer  = this.getWriter(),
              request = this.buildRequest(operation, callback, scope);

              if (operation.allowWrite()) {
                     request = writer.write(request);
              }

              Ext.apply(request, {
                     headers       : this.headers,
                     timeout       : this.timeout,
                     scope         : this,
                     callback      : this.createRequestCallback(request, operation, callback, scope),
                     method        : this.getMethod(request),
                     jsonData        : this.jsonData,
                     disableCaching: false // explicitly set it to false, ServerProxy handles caching
              });
              Ext.Ajax.request(request);               
              return request;
       },
        processResponse: function(success, operation, request, response, callback, scope){
               var me = this,
                   reader,
                   result,       
                   sorters, filters, sorterFn, records;

                     scope = scope || this;
                    
                     if (success === true) {
                   reader = me.getReader();
                   this.proxyData = me.extractResponseData(response);
                   result = reader.read(this.proxyData);
                  
                // filtering
                           filters = operation.filters;
                           if (filters.length > 0) {
                                  //at this point we have an array of  Ext.util.Filter objects to filter with,
                                  //so here we construct a function that combines these filters by ANDing them together
                                  records = [];

                                  Ext.each(result.records, function(record) {
                                         var isMatch = true,
                                         length = filters.length,
                                         i;

                                         for (i = 0; i < length; i++) {
                                                var filter = filters[i],
                                                fn     = filter.filterFn,
                                                scope  = filter.scope;

                                                isMatch = isMatch && fn.call(scope, record);
                                         }
                                         if (isMatch) {
                                                records.push(record);
                                         }
                                  }, this);

                                  result.records = records;
                                  result.totalRecords = result.total = records.length;
                           }

                           // sorting
                           sorters = operation.sorters;
                           if (sorters.length > 0) {
                                  //construct an amalgamated sorter function which combines all of the Sorters passed
                                  sorterFn = function(r1, r2) {
                                         var result = sorters[0].sort(r1, r2),
                                         length = sorters.length,
                                         i;

                                         //if we have more than one sorter, OR any additional sorter functions together
                                         for (i = 1; i < length; i++) {
                                                result = result || sorters[i].sort.call(this, r1, r2);
                                         }               

                                         return result;
                                  };

                                  result.records.sort(sorterFn);
                           }

                           // paging (use undefined cause start can also be 0 (thus false))
                           if (operation.start !== undefined && operation.limit !== undefined) {
                                  result.records = result.records.slice(operation.start, operation.start + operation.limit);
                                  result.count = result.records.length;
                           }

                   if (result.success !== false) {
                       //see comment in buildRequest for why we include the response object here
                       Ext.apply(operation, {
                           response: response,
                           resultSet: result
                       });

                       operation.commitRecords(result.records);
                       operation.setCompleted();
                       operation.setSuccessful();
                   } else {
                       operation.setException(result.message);
                       me.fireEvent('exception', this, response, operation);
                   }
               } else {
                   me.setException(operation, response);
                   me.fireEvent('exception', this, response, operation);
               }

               //this callback is the one that was passed to the 'read' or 'write' function above
               if (typeof callback == 'function') {
                   callback.call(scope || me, operation);
               }

               me.afterRequest(request, success);
           }
});