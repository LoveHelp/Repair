<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';

$userid = isset($_POST["userid"])?$_POST["userid"]:"0";

$sql=mysql_query("SELECT * FROM user WHERE userid=".$userid);
if(mysql_num_rows($sql)==1){
	while($info=mysql_fetch_assoc($sql)){
		$rows[]=$info;
	}
}
$result=json_encode($rows);

echo $result;

mysql_close();

?>
