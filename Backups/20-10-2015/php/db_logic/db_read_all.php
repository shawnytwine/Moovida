<?php
 
/*
 * Following code will list all the products
 */


// array for JSON response
$response = array();

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
$db = new DB_CONNECT($museum);

mysql_query("SET NAMES 'utf8'");
// get all products from products table
$result = mysql_query("SELECT * FROM devices") or die(mysql_error()); 
// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response["data"] = array();
 
    while ($row = mysql_fetch_array($result)) {
		if($row["device_uid"] != $device_uid)
		{
			// temp user array
			$product = array();
			$product["device_uid"] = $row["device_uid"];
			$product["latitude"] = $row["latitude"];
			$product["longitude"] = $row["longitude"];
			$product["altitude"] = $row["altitude"];		
			$product["heading"] = $row["heading"];		
			$product["timestamp"] = $row["timestamp"];
		
		
			// get all products from products table
			$sqlquery = "SELECT * FROM geolocation WHERE device = '".$row["device_uid"]."' AND status = 1";
			//echo "<br/>".$sqlquery."<br/>";
			$result_images = mysql_query($sqlquery) or die(mysql_error());
			 
			// check for empty result
			if (mysql_num_rows($result_images) > 0) {
				// looping through all results
				// products node
			 
				while ($row_2 = mysql_fetch_array($result_images)) {
					// temp user array
					$product["location"] = $row_2["location"];
				}
			}
			// push single product into final response array
			array_push($response["data"], $product);
		}
    }
    // success
    $response["success"] = 1;
	$response["message"] = "Devices data found";
    // echoing JSON response
    echo json_encode($response);
} 
else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No data found";
 
    // echo no users JSON
    echo json_encode($response);
}
?>
