<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';
//if(empty($_POST["bugtype"])) $_POST["bugtype"]="信号灯";
$sqlname="DELETE FROM equipmentrecord WHERE equipmentid=".$_GET["eid"];
mysql_query("$sqlname");
mysql_close();
echo "<script>alert('删除成功');window.location = 'equipment.php';</script>"; 
?>