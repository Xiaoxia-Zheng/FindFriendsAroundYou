
<?php

require "init.php";
$username = $_POST["username"];
$password = $_POST["password"];
$latitude = $_POST["latitude"];
$longitude = $_POST["longitude"];

#Algorithm for finding friends around me in 1 km.
$radius = 6371;
$distance = 1;

$dlat = $distance / $radius;
$dlat = rad2deg($dlat);
$maxLat = $latitude + $dlat;
$minLat = $latitude - $dlat;

$dlng = 2 * asin(sin($distance / (2 * $radius)) / cos(deg2rad($latitude)));
$dlng = rad2deg($dlng);
$maxLon = $longitude + $dlng;
$minLon = $longitude - $dlng;
       

#mysql query      
$sql_query = "SELECT username, latitude, longitude FROM group29
        Where latitude Between '$minLat' And '$maxLat'
        And longitude Between '$minLon' And '$maxLon'";

$result = mysqli_query($con, $sql_query);


#store the query result as a json data;
$users = array();
while( $row = mysqli_fetch_assoc( $result)) 
{
        $temp = array();
        $temp["username"] = $row["username"];
        $temp["latitude"] = $row["latitude"];
        $temp["longitude"] = $row["longitude"];
        $users[] = $temp;
}
echo json_encode(Array("result" => $users));

?>




