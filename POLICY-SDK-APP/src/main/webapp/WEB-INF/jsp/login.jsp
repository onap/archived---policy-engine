<%--
  ================================================================================
  onap Portal SDK
  ================================================================================
  Copyright (C) 2017 AT&T Intellectual Property
  ================================================================================
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  ================================================================================
  --%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.openecomp.portalsdk.core.util.SystemProperties" %>
<c:set var="title" value="Login" />
<c:set var="isMobileEnabled"
	value="<%=(SystemProperties.getProperty(SystemProperties.MOBILE_ENABLE)!= null && SystemProperties.getProperty(SystemProperties.MOBILE_ENABLE).trim().equals(\"true\"))%>" />

<!DOCTYPE html>
<html ng-app="abs">
	<head>
	    <title>
	      Login
        </title>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1"> 
	<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
		
		<script src="static/js/jquery-1.10.2.js" type="text/javascript"></script>
	
		<style>
		.terms {
			font-family: Verdana,Arial,Helvetica, sans-serif;
			font-size: 9px;
		}
		</style>
	</head>
	<body style="padding-top: 15px;">
		<form action="login_external" method="post">
	        <div style="position: fixed; left:15px; top:15px;z-index:-1;">
	        </div>
			<div class="centered style="-webkit-transform: translateZ(0);background:white, z-index:0;">
				<div align="center" id="errorInfo" style="display:none;float:left;font-size:12px;margin-left:5px"><span style="color:red">Invaild username or password, Please try again</span></div>
				<br/>
				<div align="center" style="margin-left:auto;margin-right:auto;width:40%;padding:6px;opacity:0.7;background-color:white">
	          		<img src="static/fusion/images/onap_trans.png"/>
					<h2> ONAP Portal </h2>
					<label>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label class="form-field__label">Login ID:</label>
					</label>
					<input  type="text" class="fn-ebz-text" name="loginId" style="width: 140px;height:25px;border-radius:7px;font-size:18px;padding-left:5px;"
						maxlength="30" />
					<br/>
					<br/>
					<label >&nbsp;Password:</label>
					<input type="password" class="span3" name="password" style="width: 140px;height:25px;border-radius:7px;font-size:18px;padding-left:5px;"
						maxlength="30" onkeydown="if (event.keyCode == 13) document.getElementById('loginBtn').click()"/> 
					<br />
					<br />
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<input id="loginBtn" type="submit" type="image" src="static/fusion/images/login_button.gif" alt="Login" />
					<br>
				</div>
			</div>
			<br/><br/><br/><br/><br/><br/><br/>
			<div id="footer">
			</div>
	    </form>
    </body>
</html>