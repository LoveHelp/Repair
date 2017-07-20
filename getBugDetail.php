<?php
header("Content-type: text/html; charset=utf-8");
$bugId = $_POST["bugid"];
include_once 'conn.php';
$sql = "SELECT b.personname AS bugfind_username,c.personname AS bugsend_username,d.personname AS repairsend_username,e.personname AS check_username,a.* FROM bugrepair a LEFT JOIN `user` b ON a.bugfinduserid = b.userid LEFT JOIN `user` c ON a.bugsenduserid = c.userid LEFT JOIN `user` d ON a.repairsenduserid = d.userid LEFT JOIN user e ON a.checkeuserid = e.userid WHERE bugid = ".$bugId;

$res = mysql_query("$sql");
$row = mysql_fetch_row($res,MYSQL_ASSOC);
$result=json_encode(array('bugDetail'=>$row));
$repairUserName="无";
if(is_array($row) && intval($row['state']) > 1){
	$repairUserName="";
	$repairuserid=$row['repairuserid'];
	$s="SELECT personname FROM user WHERE userid in (" . $repairuserid.")";
	$r = mysql_query("$s");
	while($row1=mysql_fetch_assoc($r)){
		//echo $row1["personname"];
		$repairUserName.=$row1["personname"].","; 
	}
}
$result .= "+".$repairUserName; 
echo $result;

mysql_free_result($res);
mysql_close();

?>