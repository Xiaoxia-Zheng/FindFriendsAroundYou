<?php
require "init.php";
$username = $_POST["username"];
$password = $_POST["password"];

$sql_query = "insert into group29(username, password) values('$username', '$pas$

if(mysqli_query($con, $sql_query))
{
        echo "Success";
}
else
{
        echo "error". mysqli_error($con);
}

?>