<?php 
include_once('accountinfo.php');	

if(isset($_POST["regID"]) && isset($_POST["deviceID"]) && isset($_POST["username"]))
{
	$username = $_POST["username"];
	$regID = $_POST["regID"];
	$deviceID = $_POST["deviceID"];	
	
    $conn = mysql_connect($dbhost,$dbuser,$dbpass) or die("Couldn't connect to server");
	$db = mysql_select_db($dbname,$conn) or die("Couldn't select database");
	
	$query = "INSERT INTO c2dmDemo(username,regID,deviceID) VALUES ('$username','$regID','$deviceID')";
	$result = mysql_query($query) or die("Device already registered");
	
    mysql_close($conn);
	
	echo "Success";
}
else
{
	echo "fail";
}

?>
