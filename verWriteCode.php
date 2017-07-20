<?php
header("content-type:text/html;charset=utf-8"); 
$result = "";
if( !isset($_POST['vercode']) )
{
	$vercode = $_POST['vercode'];
	$myfile = fopen("vercode.txt", "w") or die("Unable to open file!");
	fwrite($myfile, $vercode);
	fclose($myfile);
	$result = "1";
}

?>