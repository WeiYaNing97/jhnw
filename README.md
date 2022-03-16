# jhnw嘉豪交换机
## 扫描问题
###路径：
http://localhost/dev-api/sql/SwitchInteraction/logInToGetBasicInformation
###参数：
mode=telnet
ip=192.168.1.100
name=admin
password=admin
port=23
##问题列表
###路径
http://localhost/dev-api/sql/switch_problem/getUnresolvedProblemInformationByData
##解决问题(一个或者多个问题)
http://localhost/dev-api/sql/SolveProblemController/batchSolution