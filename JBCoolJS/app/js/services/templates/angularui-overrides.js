/**
 * AngularUI overrides
 */
angular.module("template/alert/alert.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("template/alert/alert.html",
     "      <div class='alert' ng-class='type && \"alert-\" + type'>\n" +
     "          <button ng-show='closeable' type='button' class='close' ng-click='close()'>Close</button>\n" +
     "          <div ng-transclude></div>\n" +
     "      </div>");
}]);

angular.module("template/timepicker/timepicker.html", []).run(["$templateCache", function($templateCache) {
	  $templateCache.put("template/timepicker/timepicker.html",
	    "<div class=\"control-group\"><input ng-class=\"{'error': invalidHours}\" type=\"text\" ng-model=\"hours\" ng-change=\"updateHours()\" class=\"span1 text-center\" ng-mousewheel=\"incrementHours()\" ng-readonly=\"readonlyInput\" maxlength=\"2\">\n" +
	    "                : \n" +
	    "<input ng-class=\"{'error': invalidMinutes}\" type=\"text\" ng-model=\"minutes\" ng-change=\"updateMinutes()\" class=\"span1 text-center\" ng-readonly=\"readonlyInput\" maxlength=\"2\"></div>\n" +
	    "");
}]);
/*
"<table class=\"form-inline\">\n" +
"        <tr>\n" +
"                <td class=\"control-group\" ng-class=\"{'error': invalidHours}\"><input type=\"text\" ng-model=\"hours\" ng-change=\"updateHours()\" class=\"span1 text-center\" ng-mousewheel=\"incrementHours()\" ng-readonly=\"readonlyInput\" maxlength=\"2\"></td>\n" +
"                <td>:</td>\n" +
"                <td class=\"control-group\" ng-class=\"{'error': invalidMinutes}\"><input type=\"text\" ng-model=\"minutes\" ng-change=\"updateMinutes()\" class=\"span1 text-center\" ng-readonly=\"readonlyInput\" maxlength=\"2\"></td>\n" +
"                <td ng-show=\"showMeridian\"><button type=\"button\" ng-click=\"toggleMeridian()\" class=\"btn text-center\">{{meridian}}</button></td>\n" +
"        </tr>\n" +
"</table>\n" +
*/