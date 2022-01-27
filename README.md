# AccessibilityService example 

 ## Filter url from browser by using accessibility service
 
 Accessibility services should only be used to assist users with disabilities in using Android devices and apps. They run in the background and receive callbacks by the system when AccessibilityEvents are fired. Such events denote some state transition in the user interface, for example, the focus has changed, a button has been clicked, etc. Such a service can optionally request the capability for querying the content of the active window. Development of an accessibility service requires extending this class and implementing its abstract methods.


use [this link](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService) to read more.

in this repository You can restrict the URLs that the user enters in their browser.


The accessibility service will look for an id of an address bar text field and try to intercept the URL from there. It is not possible to catch directly the URL which is going to be loaded. To find this id we should perform a bit of reverse engineering of the target browser: collect all fields by accessibility service and compare their ids and values with user input.
From the previous point, the next limitation is that we are going to support only the current version of the browser. The id could be changed in the future by 3rd-party browser developers and we will have to update our interceptor to continue support. This could be done either by updating an app or by providing the browser package to id mapping by the remote server
We detect either manual user input or redirect on link pressing (because in this case, the new URL will be also visible in an address bar).


#Notic
 you must have give background permission to activate the application in Xiaomi devices


 ![](https://raw.githubusercontent.com/Amir-yazdanmanesh/Accessibility-Service/master/screenshot/screenshot.gif) 
