<%--
  ================================================================================
  eCOMP Portal SDK
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
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	// Read contents of maven-generated manifest file.
	final String path = "/META-INF/MANIFEST.MF";
	java.io.InputStream input = getServletContext().getResourceAsStream(path);
	java.io.InputStreamReader reader = new java.io.InputStreamReader(input, "UTF-8");
	char [] buf = new char[1024];
	int length = reader.read(buf, 0, buf.length);
	final String manifest = new String(buf, 0, length);
	reader.close();
	input.close();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Manifest</title>
</head>
<body>
<h2>
Contents of file <%= path %>:
</h2>
<pre>
<%= manifest %>
</pre>
</body>
</html>
