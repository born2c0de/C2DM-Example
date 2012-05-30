<?php 
 
 function googleAuthenticate($username, $password, $source="Company-AppName-Version", $service="ac2dm") {    


        session_start();
        if( isset($_SESSION['google_auth_id']) && $_SESSION['google_auth_id'] != null)
            return $_SESSION['google_auth_id'];

        // get an authorization token
        $ch = curl_init();
        if(!ch){
            return false;
        }

        curl_setopt($ch, CURLOPT_URL, "https://www.google.com/accounts/ClientLogin");
        $post_fields = "accountType=" . urlencode('HOSTED_OR_GOOGLE')
            . "&Email=" . urlencode($username)
            . "&Passwd=" . urlencode($password)
            . "&source=" . urlencode($source)
            . "&service=" . urlencode($service);
        curl_setopt($ch, CURLOPT_HEADER, true);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $post_fields);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_FRESH_CONNECT, true);    
        curl_setopt($ch, CURLOPT_HTTPAUTH, CURLAUTH_ANY);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);

        // for debugging the request
        //curl_setopt($ch, CURLINFO_HEADER_OUT, true); // for debugging the request

        $response = curl_exec($ch);

        //var_dump(curl_getinfo($ch)); //for debugging the request
        //var_dump($response);

        curl_close($ch);

        if (strpos($response, '200 OK') === false) {
            return false;
        }

        // find the auth code
        preg_match("/(Auth=)([\w|-]+)/", $response, $matches);

        if (!$matches[2]) {
            return false;
        }

        $_SESSION['google_auth_id'] = $matches[2];
        return $matches[2];
    }
	
	function sendMessageToPhone($authCode, $deviceRegistrationId, $msgType, $messageText) {

        $headers = array('Authorization: GoogleLogin auth=' . $authCode);
        $data = array(
            'registration_id' => $deviceRegistrationId,
            'collapse_key' => $msgType,
            'data.payload' => $messageText //Change payload to anything you'd like. C2DM allows multiple keys for data.XXX where XXX = key           
        );

        $ch = curl_init();

        curl_setopt($ch, CURLOPT_URL, "https://android.apis.google.com/c2dm/send");
        if ($headers)
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $data);


        $response = curl_exec($ch);

        curl_close($ch);

        return $response;
    }

	if(isset($_POST["msgContent"]))
	{
		include_once('accountinfo.php');

		$username = "write2sanchit@gmail.com";
	
		$conn = mysql_connect($dbhost,$dbuser,$dbpass) or die("Couldn't connect to server");
		$db = mysql_select_db($dbname,$conn) or die("Couldn't select database");	
	
		$query = "SELECT regID FROM c2dmDemo WHERE username = '$username'";	
	
		$result = mysql_query($query) or die("Query Fail");
		$num=mysql_numrows($result);

		mysql_close($conn); 
		if($num == 1)
		{
			$msgType = "type1";
			$msgContent = $_POST["msgContent"];
			$regID = mysql_result($result,0,"regID");
			$authCode = googleAuthenticate($username,$gpass);
			echo "C2DM Server Message :<br /> " . sendMessageToPhone($authCode,$regID,$msgType,$msgContent);
		}
	}
	else
	{
		echo "Use the HTML Form to submit a C2DM message.";
	}

?>
