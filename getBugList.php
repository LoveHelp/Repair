<?php
header("Content-type: text/html; charset=utf-8");
$repairsenduserid = $_POST["userid"];
include_once 'conn.php';
$sql="SELECT bugid,bugtype,bugaddr,bugfinddescrip,bugfindtime,bugsendtime FROM bugrepair WHERE state=1 and repairsenduserid=" . $repairsenduserid." order by bugsendtime desc";

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