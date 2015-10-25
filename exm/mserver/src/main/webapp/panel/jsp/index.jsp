<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Meximus - zarządzanie ekspozycją</title>

<link rel="stylesheet" media="screen" href="../resources/css/reset.css" />
<link rel="stylesheet" media="screen" href="../resources/css/grid.css" />
<link rel="stylesheet" media="screen" href="../resources/css/style.css" />
<link rel="stylesheet" media="screen" href="../resources/css/messages.css" />
<link rel="stylesheet" media="screen" href="../resources/css/forms.css" />

<!--[if lt IE 8]>
<link rel="stylesheet" media="screen" href="css/ie.css" />
<![endif]-->

<!--[if lt IE 9]>
<script src="js/html5.js"></script>
<script src="js/PIE.js"></script>
<script src="js/IE9.js"></script>
<![endif]-->

<!-- jquerytools -->
<script type="text/javascript" src="js/jquery.tools.min.js"></script>
<script type="text/javascript" src="js/jquery.cookie.js"></script>
<script type="text/javascript" src="js/jquery.ui.min.js"></script>

<script type="text/javascript" src="js/global.js"></script>

<!-- THIS SHOULD COME LAST -->
<!--[if lt IE 9]>
<script type="text/javascript" src="js/ie.js"></script>
<![endif]-->

<script> 
$(document).ready(function(){
    $.tools.validator.fn("#username", function(input, value) {
        return value!='Username' ? true : {     
            en: "Proszę wpisać nazwę użytkownika."
        };
    });
    
    $.tools.validator.fn("#password", function(input, value) {
        return value!='Password' ? true : {     
            en: "Proszę wpisać hasło."
        };
    });

    var form = $("#form").validator({ 
    	position: 'bottom left', 
    	offset: [5, 0],
    	messageClass:'form-error',
    	message: '<div><em/></div>' // em element is the arrow
    }).attr('novalidate', 'novalidate');
});
</script> 


</head>
<body class="login">
    <div class="login-box main-content">
      <header>
          <h2>Zarządzanie ekspozycją - logowanie </h2>
      </header>
    	<section>
    		<div class="message notice">Proszę wpisać nazwę użytkownika</div>
    		<form id="form" action="dashboard.html" method="post" class="clearfix">
			<p>
				<input type="text" id="username"  class="large" value="" name="username" required="required" placeholder="Username" />
                        <input type="password" id="password" class="large" value="" name="password" required="required" placeholder="Hasło" />
                        <button class="large button button-gray fr" type="submit">Zaloguj</button>
			</p>
			<p class="clearfix">
				<span class="fl">
					<input type="checkbox" id="remember" class="" value="1" name="remember"/>
					<label class="choice" for="remember">Zapamiętaj logowanie</label>
				</span>
			</p>
		</form>
    	</section>
    </div>
</body>
</html>
