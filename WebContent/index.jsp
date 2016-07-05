<%
	if (null != request.getParameter("submitted")) com.jsantos.ps.mainwindow.LoginWindow.checkLogin(request, response);	
%>

<html>
	<head>
		<meta name="viewport" content="width=device-width, user-scalable=no" />
		<script type="text/javascript">
			function checkEnter(e){

				var characterCode;  //literal character code will be stored in this variable

				if(e && e.which){ //if which property of event object is supported (NN4)
					e = e;
					characterCode = e.which; //character code is contained in NN4's which property
				}
				else{
					e = event;
					characterCode = e.keyCode; //character code is contained in IE's keyCode property
				}

				if(characterCode == 13){ //if generated character code is equal to ascii 13 (if enter key)
					return true;
				}
				else{
					return false;
				}
			}		
		</script>
	</head>
	<body>
		<form action="index.jsp">
			<input type="hidden" name="submitted" value="true"/>		
			<div style="width:300;margin-left:auto;margin-right:auto">
				<br />
				<br />
				<br />
				<table style="background-color:lightblue;border-width:1px;border-style:solid" cellpadding="5px" cellspacing="5px" width="300px">
					<tr>
						<td align="right">store:</td>
						<td>
							<select name="storepath" style="width:180px">
								<option>/home/jsantos/store/personalserver/</option>
								<option>/home/jsantos/store/psprod/</option>
							</select>
						</td>
					</tr>
					<tr>
						<td align="right">user:</td><td><input type="text" name="username" value="pirilon" onkeypress="if (checkEnter(event)) this.form.submit();"/></td>
					</tr>
					<tr>
						<td align="right">password:</td><td><input type="password" name="password" onkeypress="if (checkEnter(event)) this.form.submit();"></input></td>
					</tr>
					<tr>
						<td align="right" colspan="2"><input type="submit" value="Ok" /></td>
					</tr>
				</table>
			</div>
		</form>
	</body>
</html>