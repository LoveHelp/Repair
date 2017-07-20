<?php
header("Content-type: text/html; charset=utf-8");
$bugId = $_POST["bugid"];
include_once 'conn.php';
$sql="SELECT a.partid,a.num AS parts_num, a.price AS parts_price, IF(a.partid <> 0,b.partname,a.partname) AS part_name, IF(a.partid <> 0,b.reference,null) AS reference FROM offer a LEFT JOIN part b ON a.partid = b.partid and a.partid <> 0 WHERE a.bugid=" . $bugId;

$res=mysql_query("$sql");
$result = null;
while($row=mysql_fetch_array($res)){
	$result[] = $row;
}
echo json_encode($result);

mysql_free_result($res);
mysql_close();

?>