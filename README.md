RxRetrofitMashup
================

This is an example of using RxJava and Retrofit to combine the results of multiple, chained, combined, asynchronous network calls into the same UI. Doing this same task using traditional Android API threading methods and state variables is fraught by peril.

This example could also be solved with Observable.zip(), which we used in an earlier example. However, .groupBy allows us to create a series of keyed, anonymous Observables which can be flatmapped and subscribed together so the code is cleaner.

Consider Observable.zip() when you want to mash up dissimilar API sources. The zip() function allows one to manipulate and transform the network results of each input type before returning a new type which can more closely match your UI's data model.
