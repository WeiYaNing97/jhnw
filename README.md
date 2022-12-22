# jhnw嘉豪交换机
- Local:   https://localhost:70/
  - Network: https://192.168.0.102:70/
## 扫描问题
###路径：
http://localhost/dev-api/sql/SwitchInteraction/logInToGetBasicInformation
###参数：
mode=telnet
ip=192.168.1.100
name=admin
password=admin
port=23
##定义分析问题的数据插入
/dev-api/sql/DefinitionProblemController/definitionProblemJsonPojo
##定义分析问题的数据回显
/dev-api/sql/DefinitionProblemController/getAnalysisList

##问题列表
###路径
http://localhost/dev-api/sql/switch_problem/getUnresolvedProblemInformationByData

##解决问题(一个或者多个问题)
http://localhost/dev-api/sql/SolveProblemController/batchSolution

##获取参数名集合
http://localhost/dev-api/sql/problem_scan_logic/getParameterNameCollection?totalQuestionTableId=2

##文件上传
###路径
Utils  ： fileCreationWrite

##问题数据插入
###路径
http://192.168.1.98/dev-api/sql/DefinitionProblemController/definitionProblemJsonPojo
@RequestBody List<String> jsonPojoList
##问题数据回显
###路径
http://192.168.1.98/dev-api/sql/DefinitionProblemController/getAnalysisList
@RequestBody String brand,@RequestBody String type,@RequestBody String firewareVersion,@RequestBody String subVersion,@RequestBody String problemName

##解决问题命令集合插入 及 问题表数据修改
###路径
/sql/command_logic/insertModifyProblemCommandSet
##解决问题命令集合 回显
###路径
/dev-api/sql/SolveProblemController/queryCommandListBytotalQuestionTableId
##解决问题命令集合修改
###路径
/sql/command_logic/updateProblemSolvingCommand