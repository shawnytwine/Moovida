<?php
 
/*
 * Following code will list all the products
 */


// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/db_connect.php';

if (isset($_POST['area_id'])) {
 
    $area_id = $_POST['area_id'];
}
else
{
	$area_id = -99;
}
if (isset($_POST['type_id'])) {
 
    $type_id = $_POST['type_id'];
}
else
{
	$type_id = -99;
}
if (isset($_POST['age_id'])) {
 
    $age_id = $_POST['age_id'];
}
else
{
	$age_id = -99;
}

// connecting to db
$db = new DB_CONNECT($museum);

mysql_query("SET NAMES 'utf8'");
// get all products from products table
$result = mysql_query("SELECT * FROM locations WHERE type ='".$area_id."' AND age = '".$age_id."' AND type = '".$type_id."'") or die(mysql_error()); 
// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response["data"] = array();
 
    while ($row = mysql_fetch_array($result)) {
		// temp user array
		$product = array();
		$product["latitude"] = $row["latitude"];
		$product["longitude"] = $row["longitude"];
		$product["altitude"] = $row["altitude"];		
		$product["name"] = $row["name"];
		$product["rating"] = $row["rating"];		
		$product["age"] = $row["age"];
		$product["pricing"] = $row["pricing"];		
		$product["type"] = $row["type"];
		$product["gender"] = $row["gender"];		
		$product["music"] = $row["music"];		
	
		// push single product into final response array
		array_push($response["data"], $product);
    }
    // success
    $response["success"] = 1;
	$response["message"] = "Locations data found";
    // echoing JSON response
    echo json_encode($response);
} 
else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No locations data found";
 
    // echo no users JSON
    echo json_encode($response);
}
?>
