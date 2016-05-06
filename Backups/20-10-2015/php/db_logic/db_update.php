	<?php
    
$response = array();
$response["success"] = -1;
	
$latitude = $_POST['latitude'];
$longitude = $_POST['longitude'];
$altitude = $_POST['altitude'];  
$heading = $_POST['heading'];  


/*ci colleghiamo al database(attenti perchè se lavorate in locale 
l'host è 10.0.2.2 e non 127.0.0.1)*/

// include db connect class
require_once __DIR__ . '/db_connect.php';
 
if (isset($_POST['device_uid'])) {
 
    $device_uid = $_POST['device_uid'];
}
else
{
	$device_uid = -99;
}

// connecting to db
$db = new DB_CONNECT();
$sqlquery = "SELECT * FROM devices WHERE device_uid ='".$device_uid."'";
$result = mysql_query($sqlquery) or die(mysql_error()); 
// check for empty result
if (mysql_num_rows($result) > 0) {
	$result = mysql_query("UPDATE devices SET latitude = $latitude, longitude = $longitude, altitude = $altitude, heading = $heading WHERE device_uid ='".$device_uid."'");
}
else
{
	$result = mysql_query("INSERT INTO devices(device_uid, latitude, longitude, altitude, heading) VALUES ('$device_uid', '$latitude', '$longitude', '$altitude', '$heading')");
}



if($result)
{
	// success
	$response["success"] = 1;
}
else
{
	// success
	$response["success"] = 0;
}
echo json_encode($response);

?>