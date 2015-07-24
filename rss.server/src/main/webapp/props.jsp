Props
<%@  page  import="java.util.Map, java.lang.String"  %> 
<%
    Map<String, String> env = System.getenv();
	out.print("Count of envs: " + env.size());
        for (String envName : env.keySet()) {
            out.print(envName + " = " + env.get(envName) + "<br/>");
        }
%>
