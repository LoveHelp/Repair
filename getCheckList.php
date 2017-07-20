<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';
$userid = $_POST["userid"];
$state = $_POST["state"];
if($state=="4"){//专人核价
	$sql="SELECT bugid,bugtype,bugaddr,bugfinddescrip,chargetime FROM bugrepair WHERE state=".$state." and repairsenduserid=".$userid;//
}else if($state =="41"){//指挥中心核价
	$sql="SELECT bugid,bugtype,bugaddr,bugfinddescrip,checktime_zr as chargetime FROM bugrepair WHERE state=".$state." and bugsenduserid=".$userid;//
}
$sql.=" order by chargetime desc";

$res=mysql_query("$sql");
if(mysql_num_rows($res) > 0){
	while($row=mysql_fetch_array($res)){
		$result[] = $row;
	}
	echo json_encode($result);
}else{
	echo "";
}

mysql_free_result($res);
mysql_close();

?>