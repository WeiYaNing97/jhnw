<template>
  <div>
    <div class="app-container" @contextmenu.prevent="showMenu($event)">
      <!--  <div class="app-container">-->
      <el-form :model="queryParams" ref="queryForm" :inline="true" :rules="rules">
        <el-form-item label="设备信息:"></el-form-item>
        <el-form-item label="品牌" prop="brand">
          <el-select v-model="queryParams.brand" placeholder="品牌"
                     filterable clearable allow-create @blur="customInput"
                     @focus="general($event)" name="brand" style="width: 150px">
            <el-option v-for="(item,index) in genList"
                       :key="index" :label="item.brand" :value="item.brand"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="型号" prop="type">
          <el-select v-model="queryParams.type" placeholder="型号"
                     filterable clearable allow-create @blur="customInput"
                     @focus="general($event)" name="type" style="width: 150px">
            <el-option v-for="(item,index) in genList"
                       :key="index" :label="item.type" :value="item.type"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="固件版本" prop="firewareVersion">
          <el-select v-model="queryParams.firewareVersion" placeholder="固件版本"
                     filterable clearable allow-create @blur="customInput"
                     @focus="general($event)" name="firewareVersion" style="width: 150px">
            <el-option v-for="(item,index) in genList"
                       :key="index" :label="item.firewareVersion" :value="item.firewareVersion"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="子版本" prop="subVersion">
          <el-select v-model="queryParams.subVersion" placeholder="子版本"
                     filterable clearable allow-create @blur="customInput"
                     @focus="general($event)" name="subVersion" style="width: 150px">
            <el-option v-for="(item,index) in genList"
                       :key="index" :label="item.subVersion" :value="item.subVersion"></el-option>
          </el-select>
        </el-form-item>
        <br/>
        <!--        //右键菜单-->
        <ul v-show="visibleItem" :style="{left:left+'px',top:top+'px'}" class="contextmenu">
          <li><el-button type="text" size="small" @click.native="handleDeleteOne">帮助</el-button></li>
          <li><el-button type="text" size="small" @click.native="handleDownloadFile">复制</el-button></li>
          <li><el-button type="text" size="small" @click.native="handlePreviewFile($event)">粘贴</el-button></li>
        </ul>
        <!--      <el-form-item>-->
        <!--        <el-select v-model="queryParams.commandId" style="width: 120px">-->
        <!--          <el-option label="所有问题" value="1"></el-option>-->
        <!--          <el-option label="未定义问题" value="0"></el-option>-->
        <!--        </el-select>-->
        <!--      </el-form-item>-->
        <el-form-item label="风险分类:"></el-form-item>
        <el-form-item label="范式分类" prop="typeProblem">
          <el-select v-model="queryParams.typeProblem" placeholder="范式分类" name="typeProblem"
                     filterable clearable allow-create @focus="generalA($event)" @blur="customInput">
            <el-option v-for="(item,index) in genListA" :key="index"
                       :label="item.typeProblem" :value="item.typeProblem"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="范式名称">
          <el-select v-model="queryParams.temProName" placeholder="请选择范式名称"
                     filterable clearable allow-create @focus="generalB($event)" name="temProName" @blur="customInput">
            <el-option v-for="(item,index) in genListB" :key="index"
                       :label="item.temProName" :value="item.temProName"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="自定义名称">
          <el-input v-model="queryParams.problemName" clearable name="problemName" placeholder="请输入问题名称"></el-input>
        </el-form-item>
        <br/>
        <el-form-item label="其它:"></el-form-item>
        <!--      <el-form-item>-->
        <!--        <el-checkbox v-model="queryParams.requiredItems">必扫问题</el-checkbox>-->
        <!--      </el-form-item>-->
        <el-form-item label="备注">
          <el-input v-model="queryParams.remarks" @focus="beizhu($event)" name="beizhu"></el-input>
        </el-form-item>
        <el-form-item label="分页符">
<!--          <el-input v-model="queryParams.notFinished"-->
<!--                    clearable-->
<!--                    @focus="biaoshi($event)" name="biaoshi"></el-input>-->

          <el-select v-model="queryParams.notFinished" placeholder="分页符"
                     filterable clearable allow-create @blur="customInput" @focus="general($event)"
                     name="notFinished" style="width: 150px">
            <el-option v-for="(item,index) in genList"
                       :key="index" :label="item.notFinished" :value="item.notFinished"></el-option>
          </el-select>

        </el-form-item>
<!--        <el-form-item>-->
<!--          <el-button @click.native="testOne" icon="el-icon-delete" size="small" plain>测试按钮</el-button>-->
<!--        </el-form-item>-->
        <!--      仔仔-->
        <!--      <el-form-item>-->
        <!--        <el-button type="primary" @click="tiwenti">提交问题</el-button>-->
        <!--      </el-form-item>-->
        <!--      <el-form-item>-->
        <!--        <el-button type="primary" @click="hebingnew">定义问题命令</el-button>-->
        <!--      </el-form-item>-->
        <!--      <el-form-item>-->
        <!--        <el-button type="primary" @click="huoquid">定义问题命令</el-button>-->
        <!--      </el-form-item>-->
              <el-form-item>
                <el-button type="primary" @click="xiangqing">定义问题详情</el-button>
              </el-form-item>
        <!--      <el-form-item>-->
        <!--        <el-button type="primary" @click="subProblem">合并按钮</el-button>-->
        <!--      </el-form-item>-->
        <!--      <el-form-item>-->
        <!--        <el-button type="primary" @click="oneone">测</el-button>-->
        <!--      </el-form-item>-->
        <!--      <el-form-item>-->
        <!--        <el-button type="primary" @click="kanuser">看用户</el-button>-->
        <!--      </el-form-item>-->
        <!--      <el-form-item>-->
        <!--      </el-form-item>-->

      </el-form>
      <hr style='border:1px inset #D2E9FF;'>

      <el-form ref="forms" :inline="true" :model="forms" v-show="chuxian">
        <el-form-item label="检测方法:"></el-form-item>
        <el-form-item>
          <el-checkbox v-model="checkedQ" @change="handleCheckAllChange">全选</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="text" icon="el-icon-delete" @click="shanchu">删除</el-button>
        </el-form-item>
        <div v-for="(item,index) in forms.dynamicItem" ref="btn" :key="index" :label="index">
          <el-form-item v-if="index!=0">
            <el-checkbox v-model="item.checked"></el-checkbox>
          </el-form-item>
          <el-form-item v-if="index!=0">{{index}}</el-form-item>
          <el-form-item v-if="index!=0" :label="numToStr(item.onlyIndex)" @click.native="wcycle(item,$event)"></el-form-item>
<!--          命令行-->
          <div v-if="item.targetType === 'command'" :key="index" style="display: inline-block">
            <el-form-item label="命令：" class="strongW" :prop="'dynamicItem.' + index + '.command'">
              <el-input v-model="item.command" name="comone" @focus="getName($event)"></el-input>
            </el-form-item>
            <el-form-item label="命令校验">
              <!--            <el-select v-model="item.jiaoyan" placeholder="校验方式">-->
              <!--              <el-option label="常规校验" value="常规校验" selected></el-option>-->
              <!--              <el-option label="自定义校验" value="自定义校验"></el-option>-->
              <!--            </el-select>-->

              <el-select v-model="item.resultCheckId" placeholder="校验方式">
                <el-option label="自定义校验" value="0"></el-option>
                <el-option label="常规校验" value="1"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <i class="el-icon-delete" @click="deleteItem(item, index)"></i>
            </el-form-item>
          </div>
<!--          匹配True-->
          <div v-else-if="item.targetType === 'match'" :key="index" style="display: inline-block" label="测试">
            <el-form-item label="匹配:" class="strongW"></el-form-item>
            <el-form-item label="位置">
              <el-select v-model="item.relativeTest" @change="relType(item)"
                         filterable allow-create placeholder="当前位置" style="width: 110px">
                <el-option label="当前位置" value="present"></el-option>
                <el-option label="全文起始" value="full"></el-option>
                <el-option label="自定义行" value="" disabled></el-option>
              </el-select>
            </el-form-item>
            <el-form-item class="blockW">
              <el-radio v-model="item.relativeType" label="present">按行匹配</el-radio>
              <el-radio v-model="item.relativeType" label="full">全文匹配</el-radio>
            </el-form-item>
            <el-form-item label="内容" :prop="'dynamicItem.' + index + '.matchContent'">
              <el-input v-model="item.matchContent"></el-input>
            </el-form-item>
            <el-form-item label="True">{{ "\xa0" }}</el-form-item>
            <el-form-item>
              <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
            </el-form-item>
          </div>
<!--          匹配False-->
          <div v-else-if="item.targetType === 'matchfal'"
               style="display: inline-block;padding-left:549px">
            <el-form-item label="False"></el-form-item>
            <el-form-item style="visibility: hidden">
              <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
            </el-form-item>
          </div>
<!--          循环-->
          <div v-else-if="item.targetType === 'wloop'" :key="index"
               style="display: inline-block">
            <el-form-item class="strongW" label="循环：" :prop="'dynamicItem.' + index + '.cycleStartId'">
              <el-input v-model="item.cycleStartId" style="width: 150px"
                        clearable @focus="shaxun"></el-input>
              <!--              disabled="true"-->
            </el-form-item>
            <el-form-item>
              <i class="el-icon-delete" @click="deleteItem(item, index)"></i>
            </el-form-item>
          </div>
<!--          可能暂时不用-->
          <div v-else-if="item.targetType === 'dimmatch'" :key="index" style="display: inline-block">
            <el-form-item label="全文模糊匹配" :prop="'dynamicItem.' + index + '.matchContent'">
              <el-input v-model="item.matchContent"></el-input>
            </el-form-item>
            <el-form-item label="光标位置">
              <el-select v-model="item.cursorRegion" placeholder="当前行" style="width: 130px">
                <el-option label="当前行" value="0"></el-option>
                <el-option label="第一行" value="1"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="True">{{ "\xa0" }}</el-form-item>
            <el-form-item>
              <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
            </el-form-item>
          </div>
          <div v-else-if="item.targetType === 'dimmatchfal'" style="display: inline-block;padding-left: 459px">
            <el-form-item label="False"></el-form-item>
            <el-form-item style="visibility: hidden">
              <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
            </el-form-item>
          </div>
          <div v-else-if="item.targetType === 'lipre'" :key="index" style="display:inline-block">
            <el-form-item label="按行精确匹配" :prop="'dynamicItem.' + index + '.relative'">
              <el-input v-model="item.relative" placeholder="下几行" style="width: 80px"></el-input>
            </el-form-item>
            <el-form-item label="匹配内容">
              <el-input v-model="item.matchContent" aria-placeholder="填写匹配内容"></el-input>
            </el-form-item>
            <el-form-item label="True">{{ "\xa0" }}</el-form-item>
            <el-form-item>
              <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
            </el-form-item>
          </div>
          <div v-else-if="item.targetType === 'liprefal'" style="display: inline-block;padding-left: 466px">
            <el-form-item label="False"></el-form-item>
            <el-form-item style="visibility: hidden">
              <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
            </el-form-item>
          </div>
          <div v-else-if="item.targetType === 'dimpre'" :key="index" style="display: inline-block">
            <el-form-item label="按行模糊匹配" :prop="'dynamicItem.' + index + '.relative'">
              <el-input v-model="item.relative" placeholder="下几行" style="width: 80px"></el-input>
            </el-form-item>
            <el-form-item label="匹配内容">
              <el-input v-model="item.matchContent" aria-placeholder="填写匹配内容"></el-input>
            </el-form-item>
            <el-form-item label="True">{{ "\xa0" }}</el-form-item>
            <el-form-item>
              <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
            </el-form-item>
          </div>
          <div v-else-if="item.targetType === 'dimprefal'" style="display: inline-block;padding-left: 466px">
            <el-form-item label="False"></el-form-item>
            <el-form-item style="visibility: hidden">
              <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
            </el-form-item>
          </div>
<!--          取参-->
          <div v-else-if="item.targetType === 'takeword'" :key="index" style="display: inline-block">
            <el-form-item label="取参:" class="strongW"></el-form-item>
            <el-form-item label="位置">
              <el-select v-model="item.cursorRegion" placeholder="当前位置" style="width: 110px">
                <el-option label="当前位置" value="0"></el-option>
                <el-option label="全文起始" value="1"></el-option>
                <!--              <el-option label="自定义行" value="ding" disabled></el-option>-->
              </el-select>
            </el-form-item>
            <el-form-item :prop="'dynamicItem.' + index + '.takeword'">
              <!--            <el-input v-model="item.relative" placeholder="下几行" style="width: 80px"></el-input>-->
              <el-input v-model="item.relative" style="width: 80px" placeholder="行偏移"></el-input> --
              <el-input v-model="item.rPosition" style="width: 80px" placeholder="列偏移"></el-input> --
              <el-input v-model="item.length1" style="width: 80px" placeholder="取几个"></el-input>
              <el-select v-model="item.classify" @change="reloadv" placeholder="词汇/单字/字符串" style="width: 80px">
                <el-option label="词汇" value="W"></el-option>
                <el-option label="单字" value="L"></el-option>
                <el-option label="字符串" value="S"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-radio-group v-model="item.exhibit">
                <el-radio label="显示" style="margin-right: 5px"></el-radio>
                <el-input style="width: 120px" placeholder="参数名" v-model="item.wordName" v-if="item.exhibit==='显示'"></el-input>
                <el-radio label="不显示" style="margin-right: 5px"></el-radio>
                <el-input style="width: 120px" placeholder="参数名" v-model="item.wordName" v-if="item.exhibit==='不显示'"></el-input>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <i class="el-icon-delete" @click="deleteItem(item, index)"></i>
            </el-form-item>
          </div>
<!--         比较 -->
          <div v-else-if="item.targetType === 'analyse'" :key="index" style="display:inline-block">
            <el-form-item label="比较" v-show="bizui">
              <el-input v-model="item.compare" style="width: 217px" v-show="bizui" @input="bihou"></el-input>
            </el-form-item>
            <el-form-item label="比较" v-show="bixiala">
              <el-select v-model="item.bi" filterable @change="bibi"
                         v-show="bixiala" placeholder="例如:品牌<5.20.99">
                <el-option v-for="(item,index) in biList"
                           :label="item.valueOf(index)" :value="item.valueOf(index)"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="True">{{ "\xa0" }}</el-form-item>
            <el-form-item>
              <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
            </el-form-item>
          </div>
          <div v-else-if="item.targetType === 'analysefal'" style="display: inline-block;padding-left: 268px">
            <el-form-item label="False"></el-form-item>
            <el-form-item style="visibility: hidden">
              <i class="el-icon-delete" @click="deleteItemp(item, index)"></i>
            </el-form-item>
          </div>

          <div v-else-if="item.targetType === 'prodes'" :key="index" style="display:inline-block">
            <el-form-item label="异常：" class="strongW">
              <!--            prop="'dynamicItem.' + index + '.prodes'"-->
              <!--            <el-input v-model="item.prodes"></el-input>-->
              <el-select v-model="item.tNextId" filterable allow-create @blur="proNameShu" placeholder="异常、安全、完成、自定义">
                <el-option label="异常" value="有问题"></el-option>
                <el-option label="安全" value="无问题"></el-option>
                <el-option label="完成" value="完成"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <i class="el-icon-delete" @click="deleteItem(item, index)"></i>
            </el-form-item>
          </div>

          <el-form-item>
            <el-dropdown trigger="click">
              <el-button type="primary"><i class="el-icon-plus"></i></el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>
                  <el-button @click="addItem('command',item)" type="primary">命令</el-button>
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-button @click="addItem('match',item,index)" type="primary">匹配</el-button>
                </el-dropdown-item>
                <!--              <el-dropdown-item>-->
                <!--                <el-button @click="addItem('dimmatch',item)" type="primary">全文模糊匹配</el-button>-->
                <!--              </el-dropdown-item>-->
                <!--              <el-dropdown-item>-->
                <!--                <el-button @click="addItem('lipre',item)" type="primary">按行精确匹配</el-button>-->
                <!--              </el-dropdown-item>-->
                <!--              <el-dropdown-item>-->
                <!--                <el-button @click="addItem('dimpre',item)" type="primary">按行模糊匹配</el-button>-->
                <!--              </el-dropdown-item>-->
                <el-dropdown-item>
                  <el-button @click="addItem('takeword',item)" type="primary">取参</el-button>
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-button @click="addItem('analyse',item)" type="primary">分析比较</el-button>
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-button @click="addItem('wloop',item)" type="primary">循环位置</el-button>
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-button @click="addItem('prodes',item)" type="primary">问题名称</el-button>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-form-item>
        </div>

        <el-form-item>
          <!--        <el-button @click="tijiao" type="primary">提交</el-button>-->
          <el-button @click="subProblem" type="primary">提交</el-button>
          <!--        <el-button @click="testAll" type="primary">测试</el-button>-->
          <!--        <el-button @click="jiejue" type="primary">解决问题</el-button>-->
          <!--        <el-button @click="proname" type="primary">问题名</el-button>-->
        </el-form-item>

      </el-form>

      <TinymceEditor v-show="showha" ref="fuwenben"></TinymceEditor>
      <el-button @click="yinyin" v-show="showha">隐藏</el-button>
      <el-button @click="look" type="primary" v-show="showha" style="margin-top:20px">提交</el-button>

      <el-input type="textarea" v-show="partShow" v-model="particular" rows="15"></el-input>
      <el-button @click="partsub" type="primary" v-show="partShow" style="margin-top:20px">提交详情</el-button>
      <el-button @click="partclose" type="primary" v-show="partShow" style="margin-top:20px">关闭详情</el-button>

<!--      <el-dialog-->
<!--        title="提示"-->
<!--        :visible.sync="dialogVisibleHelp"-->
<!--        style="padding: 10px"-->
<!--        width="40%">-->
<!--        <div id="fatherq" class="qqqqq">-->
<!--          <h3 style="font-weight: bolder" id="brand" ref="brand">品牌</h3>-->
<!--          &lt;!&ndash;          <p>{{ this.baseConfig.brand }}</p>&ndash;&gt;-->
<!--          &lt;!&ndash;          <p>请输入版本:</p>&ndash;&gt;-->
<!--          <h3 style="font-weight: bolder" id="type" ref="type">型号</h3>-->
<!--          <p>请输入型号:</p>-->
<!--          <h3 style="font-weight: bolder" id="firewareVersion" ref="firewareVersion">固件版本</h3>-->
<!--          <p>请输入固件版本:</p>-->
<!--          <h3 style="font-weight: bolder" id="subVersion" ref="subVersion">子版本</h3>-->
<!--          <p>请输入子版本:</p>-->
<!--          <h3 style="font-weight: bolder" id="typeProblem" ref="typeProblem">范式分类</h3>-->
<!--          <p>请输入范式类型:</p>-->
<!--          <h3 style="font-weight: bolder" id="temProName" ref="temProName">范式名称</h3>-->
<!--          <p>请输入范式名称:</p>-->
<!--          <h3 style="font-weight: bolder" id="problemName" ref="problemName">自定义名称</h3>-->
<!--          <p>请输入自定义名称:</p>-->
<!--          <h3 style="font-weight: bolder" id="beizhu" ref="beizhu">备注</h3>-->
<!--          <p>请输入备注:</p>-->
<!--          <h3 style="font-weight: bolder" id="biaoshi" ref="biaoshi">标识符</h3>-->
<!--          <p>请输入标识符:</p>-->
<!--          <h3 style="font-weight: bolder" id="comone" ref="comone">命令</h3>-->
<!--          <p>请输入命令:</p>-->
<!--          <a :href="whelp" style="display: none" ref="bang"><span>阿斯顿撒大</span></a>-->
<!--        </div>-->
<!--        <span slot="footer" class="dialog-footer">-->
<!--    <el-button @click="dialogVisibleHelp = false">取 消</el-button>-->
<!--    <el-button type="primary" @click="dialogVisibleHelp = false">确 定</el-button>-->
<!--  </span>-->
<!--      </el-dialog>-->

        <el-dialog
        title="提示"
        v-dialog-drag
        :visible.sync="dialogVisibleHelp"
        style="padding: 10px"
        width="40%">
        <div id="fatherq" class="qqqqq">
          <h3 style="font-weight: bolder" id="brand" ref="brand">品牌</h3>
<!--          <p>{{ this.baseConfig.brand }}</p>-->
<!--          <p>请输入版本:</p>-->
          <h3 style="font-weight: bolder" id="type" ref="type">型号</h3>
          <p>请输入型号:</p>
          <h3 id="firewareVersion" ref="firewareVersion">固件版本</h3>
          <p>请输入固件版本:</p>
          <h3 style="font-weight: bolder" id="subVersion" ref="subVersion">子版本</h3>
          <p>请输入子版本:</p>
          <h3 style="font-weight: bolder" id="typeProblem" ref="typeProblem">范式分类</h3>
          <p>请输入范式类型:</p>
          <h3 style="font-weight: bolder" id="temProName" ref="temProName">范式名称</h3>
          <p>请输入范式名称:</p>
          <h3 style="font-weight: bolder" id="problemName" ref="problemName">自定义名称</h3>
          <p>请输入自定义名称:</p>
          <h3 style="font-weight: bolder" id="beizhu" ref="beizhu">备注</h3>
          <p>请输入备注:</p>
          <h3 style="font-weight: bolder" id="biaoshi" ref="biaoshi">标识符</h3>
          <p>请输入标识符:</p>
          <h3 style="font-weight: bolder" id="comone" ref="comone">命令</h3>
          <p>请输入命令:</p>
          <a :href="whelp" style="display: none" ref="bang"><span>阿斯顿撒大</span></a>
        </div>
        <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisibleHelp = false">取 消</el-button>
    <el-button type="primary" @click="dialogVisibleHelp = false">确 定</el-button>
  </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { listJh_test1, getJh_test1, delJh_test1, addJh_test1, updateJh_test1, exportJh_test1 } from "@/api/sql/jh_test1";
import TinymceEditor from "@/components/Tinymce/TinymceEditor"
import request from '@/utils/request'
import router from '@/router/index'
// import help_txt from "../help_txt/help_txt";
// import help_txt from "../help_txt/help_txt"
export default {
  name: "Jh_test1",
    inject:["reload"],
    components:{
        TinymceEditor,
        // help_txt
    },
  data() {
    return {
        //右键菜单
        visibleItem:false,
        top:0,
        left:0,
        //联动
        clickLine:'',
        keys:1,
        //帮助
        help_show:false,
        whelp:'',
        dialogVisibleHelp:false,
        //问题详情
        particular:'',
        partShow:false,
        //必选项
        //隐藏定义问题
        chuxian:true,
        display:'inline-block',
        paddingLeft:'0px',
        cpus:'',
        who:'',
        helpT:'',
        showha:false,
        bizui:false,
        bixiala:true,
        radio:'1',
        //通用基本信息下拉集合
        genList:[],
        genListA:[],
        genListB:[],

        biList:['品牌','型号','固件版本','子版本'],
        checkedQ:false,
        // checked:false,
        //问题ID
        proId:'',
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 是否显示弹出层
      open: false,
        position:0,
      // 查询参数
      queryParams: {
          brand: '',
          type: '',
          firewareVersion: '',
          subVersion: '',
          // commandId:'1',
          problemName:'',
          notFinished:'---- More ----',
          // notFinished:'',
          typeProblem:'',
          temProName:'',
          //是否必扫,先注释保留
          requiredItems:false,
          remarks:''
      },
        // 表单校验
        rules: {
            brand:[
                { required: true, message: '请输入品牌', trigger: 'change'}
            ]
        },
      // 表单参数
        form:{},
        forms: {
          dynamicItem:[
              {
                  test:'test',
                  onlyIndex:''
              }
          ],
      },
        flagchange:0,
    };
  },
    // 自动触发事件
    directives:{
        trigger:{
            inserted(el,binging){
                el.click()
            }
        }
    },
    watch:{
        // 监听 visible，来触发关闭右键菜单，调用关闭菜单的方法
        visibleItem(value) {
            if (value) {
                document.body.addEventListener('click', this.closeMenu)
            } else {
                document.body.removeEventListener('click', this.closeMenu)
            }
        }
    },
  methods: {
      //无用代码⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇
      //获取名字
      getName(e){
          this.who = e.target.getAttribute('name')
          console.log(this.who)
      },
      //无用代码⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆
      //自定义下拉框输入通用
      customInput(e){
          this.who = e.target.getAttribute('name')
          let value = e.target.value
          for (let key in this.queryParams){
              if (this.queryParams.hasOwnProperty(key)){
                  if (key == this.who){
                      if (value){
                          this.queryParams[key] = value
                      }
                  }
              }
          }
      },
      //下拉框获取数据通用
      general(e){
          this.who = e.target.getAttribute('name')
          // console.log('当前点击输入框' + this.who)
          //查询对象
          let newPar = {
              brand:this.queryParams.brand,
              type:this.queryParams.type,
              firewareVersion:this.queryParams.firewareVersion,
              subVersion:this.queryParams.subVersion
          }
          for (var key in newPar){
              if (this.who == key){
                  newPar[key] = ''
              }
          }
          return request({
              url:'/sql/total_question_table/selectPojoList',
              method:'get',
              params:newPar
          }).then(response=>{
              this.genList = this.quchong(response,this.who)
          })
      },
      //测试
      testOne(){
          this.help_show = true
          this.dialogVisibleHelp = true
      },
      //右键帮助
      handleDeleteOne(){
          this.dialogVisibleHelp = true
          this.whelp = `#${this.who}`
          console.log(this.whelp)
          setTimeout(()=>{
              this.$refs.bang.click()
              let h3l = document.getElementById('fatherq').getElementsByTagName('h3').length
              for (let i = 0;i<h3l;i++){
                  // console.log(document.getElementById('fatherq').getElementsByTagName('h3')[i])
                  document.getElementById('fatherq').getElementsByTagName('h3')[i].classList.remove('redColor')
              }
              this.$refs[this.who].classList.add('redColor')
          },0)
          this.visibleItem = false;
      },
      //复制功能
      handleDownloadFile(){
          let copyInput = document.createElement('input')
          var text = ''
          if(window.getSelection()){
              console.log(window.getSelection())
              text = window.getSelection().toString()
          }else if (document.selection && document.selection.type != 'Control'){
              text = document.selection.createRange().text
          }
          copyInput.value = text
          document.body.appendChild(copyInput)
          copyInput.select()
          // document.execCommand('Copy')
          navigator.clipboard.writeText(text)
          copyInput.remove();
          console.log(text)
      },
      //粘贴功能
      handlePreviewFile(e){
          console.log(this.who)
          navigator.clipboard.readText().then(text=>{
              console.log(text)
              document.getElementsByName(this.who)[0].value = text
          })
      },
      closeMenu(){
          this.visibleItem = false;
      },
      //重新赋值
      reloadv(){

      },
      //下载
      kanuser(){
          var ip = window.location.host
          console.log(ip)
          console.log("ip",ip.split(":")[0])
          console.log(process.env.VUE_APP_HOST)
      },
      //
      beizhu(e){
          this.who = e.target.getAttribute('name')
          console.log(this.who)
      },
      //右键
      biaoshi(e){
          this.who = e.target.getAttribute('name')
          console.log(this.who)
      },
      yinyin(){
        this.showha = false
      },
      //右键
      showMenu(){
          this.visibleItem = true
          console.log('右击')
          this.top = event.clientY
          this.left = event.clientX
      },
      //获取选中信息
      getSelection(){
          if(window.getSelection()){
              console.log(window.getSelection())
             var text = window.getSelection().toString()
              console.log(text)
          }else if (document.selection && document.selection.type != 'Control'){
              text = document.selection.createRange().text
          }
          if (text){
              return text
          }
      },
      shaxun(){
          // if (item.cycleStartId.length == 0){
          //     this.onfocus('clear')
          // }
      },
      //通用数组对象去重
      quchong(arr,key){
          let ret = []
          arr.forEach((item,index,self)=>{
              let compare = []
              ret.forEach((retitem,retindex,retself)=>{
                  compare.push(retitem[key])
              })
              if (compare.indexOf(item[key]) === -1){
                  ret.push(item)
              }
          })
          return ret
      },
      //范式分类
      generalA(e){
          this.who = e.target.getAttribute('name')
          console.log(this.who)
          let newPar = {}
          return request({
              url:'/sql/total_question_table/selectPojoList',
              method:'get',
              data: newPar
          }).then(response=>{
              // console.log(response)
              this.genListA = this.quchong(response,this.who)
          })
      },
      //范式名称
      generalB(e){
          if (this.queryParams.typeProblem === ''){
              this.$message.warning('请先选择范式分类!')
          }else {
              this.who = e.target.getAttribute('name')
              console.log(this.who)
              let newPar = {
                  typeProblem:this.queryParams.typeProblem
              }
              return request({
                  url:'/sql/total_question_table/selectPojoList',
                  method:'get',
                  params:newPar
              }).then(response=>{
                  console.log(response)
                  //范式
                  this.genListB = this.quchong(response,this.who)
              })
          }
      },

      //比较选中触发
      bibi(){
          this.bizui = true
          this.forms.dynamicItem.forEach(e=>{
              if (e.targetType === 'analyse'){
                  const bixuan = e.bi
                  this.$set(e,'compare',bixuan)
              }
          })
          this.bixiala = false
      },
      //比较输入框为空后
      bihou(){
        this.forms.dynamicItem.forEach(e=>{
            if (e.targetType === 'analyse'){
                if (e.compare === ''){
                    this.bixiala = true
                    this.bizui = false
                }
            }
        })
      },
      //有无问题、自定义输入
      proNameShu(e){
          let value = e.target.value
          // if(value){
          //    item.tNextId = value
          // }
      },
      biShu(e){
          let value = e.target.value
          if(value){
              // this.forms.compareThree = value
              this.forms.dynamicItem.compareThree = value
          }
      },
      //1.31
      oneone(){
          const useForm = []
          const useLess = []
          this.forms.dynamicItem.forEach(e=>{
              if (e.test === "test"){
                  useLess.push(e)
              }else {
                  useForm.push(e)
              }
          })
          const handForm = useForm.map(x => JSON.stringify(x))
          console.log(handForm)
          if (handForm.length < 1){
              console.log('xxx')
              this.$message.error('sss')
          }else {
              console.log('www')
          }
      },
      //测试
      testAll(){
          this.forms.dynamicItem.forEach(eeee=>{
              if (eeee.targetType == 'match'){
                  this.$set(eeee,'relative',eeee.relativeTest + '&' + eeee.relativeType)
                  var sssList = eeee.relative.split('&')
                  console.log(sssList[0])
              }
          })
          console.log(this.forms.dynamicItem)
      },
      //New定义问题合并提交
      subProblemNew(){
          var subNewPro = JSON.parse(JSON.stringify(this.queryParams))
          if (subNewPro.requiredItems === true){
              this.$set(subNewPro,'requiredItems','1')
          }else {
              this.$set(subNewPro,'requiredItems','0')
          }
          for (let i in subNewPro){
              if (subNewPro[i] === 'null'){
                  subNewPro[i] = ''
              }
          }
          if (subNewPro.brand != '' && subNewPro.typeProblem != '' && subNewPro.temProName != '' && this.forms.dynamicItem.length > 1){
              return request({
                  url:'/sql/total_question_table/add',
                  method:'post',
                  data:JSON.stringify(subNewPro)
              }).then(response=>{
                  console.log(response)
                  this.proId = response.msg
                  console.log(typeof this.proId)
                  // if (typeof(this.proId) === 'number'){
                  if (this.proId != ''){
                      console.log('数字')
                      const useForm = []
                      const useLess = []
                      this.forms.dynamicItem.forEach(e=>{
                          if (e.test === "test"){
                              useLess.push(e)
                          }else {
                              useForm.push(e)
                          }
                      })
                      useForm.forEach(eeee=>{
                          const thisIndex = useForm.indexOf(eeee)
                          if(useForm.length != thisIndex+1){
                              const thisNext = useForm[thisIndex+1]
                              this.$set(eeee,'nextIndex',thisNext.onlyIndex)
                          }
                          // if (eeee.matched === '全文精确匹配'){
                          //     if (eeee.cursorRegion === 'D'){
                          //         this.$set(eeee,'cursorRegion','0')
                          //     }else if (eeee.cursorRegion === 'Q'){
                          //         this.$set(eeee,'cursorRegion','1')
                          //     }else {
                          //         console.log('我')
                          //     }
                          // }
                          if (eeee.action === '取词'){
                              eeee.length = `${eeee.length1}${eeee.classify}`
                              if (eeee.length === 'undefinedundefined'){
                                  eeee.length = '0'
                              }else {
                                  eeee.length = `${eeee.length1}${eeee.classify}`
                              }
                          }
                          if (eeee.targetType == 'match'){
                              this.$set(eeee,'relative',eeee.relativeTest + '&' + eeee.relativeType)
                          }
                          this.$set(eeee,'pageIndex',thisIndex+1)
                          if (eeee.targetType == 'takeword'){
                              const takeWordt = useForm.indexOf(eeee)
                              var quciC = ''
                              useForm.map((e11)=>{
                                  const takeWl = useForm.indexOf(e11)
                                  if(takeWl == takeWordt-1){
                                      quciC = e11.matchContent
                                  }
                              })
                              this.$set(eeee,'matchContent',quciC)
                          }
                          //拆分有无问题
                          if (eeee.action === '问题'){
                              const neid = eeee.action
                              console.log(neid)
                              const thisData = Date.now()
                              console.log(thisData)
                          }
                      })
                      const handForm = useForm.map(x => JSON.stringify(x))
                      console.log(handForm)
                      if (handForm.length < 1){
                          this.$message.warning('分析逻辑不能为空!')
                      }else {
                          return request({
                              url:`/sql/DefinitionProblemController/definitionProblemJsonPojo?totalQuestionTableId=${this.proId}`,
                              method:'post',
                              data:handForm
                          }).then(response=>{
                              console.log(response)
                              this.$message.success('提交问题成功!')
                              this.reload()
                              router.push({
                                  path:'/sql/look_test'
                              })
                          })
                      }
                  }else {
                      this.$message.error('提交问题失败!')
                  }
                  this.chuxian = true
              })
          }else {
              alert('品牌、范式分类、范式名称、分析逻辑不得为空!')
          }
      },
      //定义问题合并提交
      subProblem(){
          var subNewPro = JSON.parse(JSON.stringify(this.queryParams))
          if (subNewPro.requiredItems === true){
              this.$set(subNewPro,'requiredItems','1')
          }else {
              this.$set(subNewPro,'requiredItems','0')
          }
          for (let i in subNewPro){
              if (subNewPro[i] === 'null'){
                  subNewPro[i] = ''
              }
          }
          if (subNewPro.brand != '' && subNewPro.typeProblem != '' && subNewPro.temProName != '' && this.forms.dynamicItem.length > 1){
              return request({
                  url:'/sql/total_question_table/add',
                  method:'post',
                  data:JSON.stringify(subNewPro)
              }).then(response=>{
                  console.log(response)
                  this.proId = response.msg
                  console.log(typeof this.proId)
                  // if (typeof(this.proId) === 'number'){
                  if (this.proId != ''){
                      console.log('数字')
                      const useForm = []
                      const useLess = []
                      this.forms.dynamicItem.forEach(e=>{
                          if (e.test === "test"){
                              useLess.push(e)
                          }else {
                              useForm.push(e)
                          }
                      })
                      useForm.forEach(eeee=>{
                          const thisIndex = useForm.indexOf(eeee)
                          if(useForm.length != thisIndex+1){
                              const thisNext = useForm[thisIndex+1]
                              this.$set(eeee,'nextIndex',thisNext.onlyIndex)
                          }
                          // if (eeee.matched === '全文精确匹配'){
                          //     if (eeee.cursorRegion === 'D'){
                          //         this.$set(eeee,'cursorRegion','0')
                          //     }else if (eeee.cursorRegion === 'Q'){
                          //         this.$set(eeee,'cursorRegion','1')
                          //     }else {
                          //         console.log('我')
                          //     }
                          // }
                          if (eeee.action === '取词'){
                              eeee.length = `${eeee.length1}${eeee.classify}`
                              if (eeee.length === 'undefinedundefined'){
                                  eeee.length = '0'
                              }else {
                                  eeee.length = `${eeee.length1}${eeee.classify}`
                              }
                          }
                          if (eeee.targetType == 'match'){
                              this.$set(eeee,'relative',eeee.relativeTest + '&' + eeee.relativeType)
                          }
                          this.$set(eeee,'pageIndex',thisIndex+1)
                          if (eeee.targetType == 'takeword'){
                              const takeWordt = useForm.indexOf(eeee)
                              var quciC = ''
                              useForm.map((e11)=>{
                                  const takeWl = useForm.indexOf(e11)
                                  if(takeWl == takeWordt-1){
                                      quciC = e11.matchContent
                                  }
                              })
                              this.$set(eeee,'matchContent',quciC)
                          }
                          //拆分有无问题
                          if (eeee.action === '问题'){
                              const neid = eeee.action
                              console.log(neid)
                              const thisData = Date.now()
                              console.log(thisData)
                          }
                      })
                      const handForm = useForm.map(x => JSON.stringify(x))
                      console.log(handForm)
                      if (handForm.length < 1){
                          this.$message.warning('分析逻辑不能为空!')
                      }else {
                          return request({
                              url:`/sql/DefinitionProblemController/definitionProblemJsonPojo?totalQuestionTableId=${this.proId}`,
                              method:'post',
                              data:handForm
                          }).then(response=>{
                              console.log(response)
                              this.$message.success('提交问题成功!')
                              this.reload()
                              router.push({
                                  path:'/sql/look_test'
                              })
                          })
                      }
                  }else {
                      this.$message.error('提交问题失败!')
                  }
                  this.chuxian = true
              })
          }else {
              alert('品牌、范式分类、范式名称、分析逻辑不得为空!')
          }
      },
      //合并按钮新的
      hebingnew(){
          var shasha = JSON.parse(JSON.stringify(this.queryParams))
          this.$delete(shasha,'commandId')
          if (shasha.requiredItems === true){
              this.$set(shasha,'requiredItems','1')
          }else {
              this.$set(shasha,'requiredItems','0')
          }
          for (let i in shasha){
              if (shasha[i] === 'null'){
                  shasha[i] = ''
              }
          }
          console.log(shasha)
          if (shasha.brand === '' && shasha.typeProblem === '' && shasha.temProName === ''){
              alert('品牌、范式分类、范式名称 不得为空!')
          }else {
              return request({
                  url:'/sql/total_question_table/add',
                  method:'post',
                  data:JSON.stringify(shasha)
              }).then(response=>{
                  console.log(response)
                  this.$message.success('提交问题成功!')
                  this.proId = response.msg
                  this.chuxian = true
              })
          }
      },
      //提交问题，返回问题ID
      tiwenti(){
          var shasha = JSON.parse(JSON.stringify(this.queryParams))
          this.$delete(shasha,'commandId')
          if (shasha.requiredItems === true){
              this.$set(shasha,'requiredItems','1')
          }else {
              this.$set(shasha,'requiredItems','0')
          }
          return request({
              url:'/sql/total_question_table/add',
              method:'post',
              data:JSON.stringify(shasha)
          }).then(response=>{
              this.$message.success('提交问题成功!')
              this.proId = response.msg
          })
      },
      //查看是否存在，返回问题ID，定义命令隐藏变为出现
      huoquid(){
          let form = new FormData();
          for (var key in this.queryParams){
              if (key != 'notFinished'&&key != 'requiredItems'&&key != 'commandId'&&key != 'remarks'){
                  form.append(key,this.queryParams[key])
              }
          }
          return request({
              url:'/sql/total_question_table/totalQuestionTableId',
              method:'post',
              data:form
          }).then(response=>{
              console.log(response)
              if (typeof(response) === 'number'){
                  this.chuxian = true
                  this.proId = response
              }else {
                  this.$message.error('没有定义该问题,请先定义问题在定义命令！')
              }
          })
      },
      //定义问题详情
      xiangqing(){

          this.partShow = true

          let form = new FormData();
          for (var key in this.queryParams){
              // if (key != 'notFinished'&&key != 'requiredItems'&&key != 'commandId'&&key != 'remarks'){
                  if (key != 'notFinished'&&key != 'requiredItems'&&key != 'remarks'){
                  form.append(key,this.queryParams[key])
              }
          }
          console.log(form)
          // return request({
          //     url:'/sql/total_question_table/totalQuestionTableId',
          //     method:'get',
          //     data:form
          // }).then(response=>{
          //     console.log(response)
          //     if (typeof(response) === 'number'){
          //         this.particular = ''
          //         this.partShow = true
          //         this.proId = response
          //     }else {
          //         this.$message.error('没有定义该问题,请先定义问题！')
          //     }
          // })
      },
      //富文本提交问题详情
      look(){
          console.log(this.proId)
          console.log(this.$refs.fuwenben.geifu())
          return request({
              // url:`/sql/problem_describe/insertProblemDescribe?totalQuestionTableId=${this.proId}`,
              method:'post',
              data:this.$refs.fuwenben.geifu()
          }).then(response=>{
              this.$message.success('问题详情已提交!')
          })
      },
      //普通文本提交问题详情
      partsub(){
          console.log(this.proId)
          console.log(this.particular)
          return request({
              url:`/sql/problem_describe/insertProblemDescribe?totalQuestionTableId=${this.proId}`,
              method:'post',
              data:this.particular
          }).then(response=>{
              this.$message.success('问题详情已提交!')
              this.partShow = false
          })
      },
      //关闭详情
      partclose(){
          this.particular = ''
          this.partShow = false
      },
      //全选
      handleCheckAllChange() {
          const useForm = []
          const useLess = []
          this.forms.dynamicItem.forEach(e=>{
              if (e.test === "test"){
                  useLess.push(e)
              }else {
                  useForm.push(e)
              }
          })
          const checkT = []
          useForm.forEach(te=>{
              if (te.checked === true){
                  checkT.push(te)
              }
          })
          if (checkT.length < useForm.length){
              useForm.forEach(tte=>{
                  this.$set(tte,'checked',true)
              })
              this.checkedQ = true
          }else if (checkT.length === useForm.length){
              useForm.forEach(ttte=>{
                  this.$set(ttte,'checked',false)
              })
              this.checkedQ = false
          }
      },
      shanchu(){
          const shanLiu = this.forms.dynamicItem.filter(shan=>shan.checked != true)
          this.forms.dynamicItem = shanLiu
          this.forms.dynamicItem.forEach(liu=>{
              this.$set(liu,'checked',false)
          })
      },

      //循环项获取index
      wcycle(item,event){
          const cycleId = item.onlyIndex
          console.log(item)
          this.forms.dynamicItem.forEach(cy=>{
              if (cy.hasOwnProperty('cycleStartId') != true && cy.targetType == 'wloop'){
                  if (item.relativeTest == 'full'){
                      this.$confirm('此循环行涉及全文起始，应修改为当前位置!', '提示', {
                          confirmButtonText: '确定',
                          cancelButtonText: '取消',
                          type: 'warning'
                      }).then(() => {
                          // this.$message({
                          //     type: 'success',
                          //     message: '删除成功!'
                          // });
                      }).catch(() => {
                          // this.$message({
                          //     type: 'info',
                          //     message: '已取消删除'
                          // });
                      });
                  }
                  this.$set(cy,'cycleStartId',cycleId)
              }else if (cy.cycleStartId == ''){
                  if (item.relativeTest == 'full'){
                      this.$confirm('此循环行涉及全文起始，应修改为当前位置!', '提示', {
                          confirmButtonText: '确定',
                          cancelButtonText: '取消',
                          type: 'warning'
                      }).then(() => {
                          // this.$message({
                          //     type: 'success',
                          //     message: '删除成功!'
                          // });
                      }).catch(() => {
                          // this.$message({
                          //     type: 'info',
                          //     message: '已取消删除'
                          // });
                      });
                  }
                  this.$set(cy,'cycleStartId',cycleId)
              }
          })
      },
      numToStr(num){
          num = num.toString()
          return num
      },
      //全文、按行匹配 联动
      relType(item){
          this.clickLine = item.onlyIndex
          this.forms.dynamicItem.forEach(e=>{
              if (e.targetType == 'match' && e.onlyIndex == this.clickLine){
                  if (e.relativeTest == 'full'){
                      this.$set(e,'relativeType','full')
                  }else if (e.relative == 'present'){
                      this.$set(e,'relativeType','present')
                  }else {
                      this.$set(e,'relativeType','present')
                  }
              }
          })
      },
      //New 添加表单项
      addItemNew(){
          const thisData = Date.now()
          const item1 = {
              targetType: type,
              onlyIndex:thisData,
              trueFalse:""
          }
          this.$set(item1,'checked',false)
          const thisIndex = this.forms.dynamicItem.indexOf(item)
          console.log(thisIndex)
          //动态添加的样式空格问题
          // console.log(this.$refs.btn[thisIndex])
          // console.log(item)
          // this.$set(item,'nextIndex',thisData)
          if (type == 'command'){
              this.$set(item1,'resultCheckId','0')
          }
          if(type == 'match'){
              this.$refs.btn[thisIndex].labelss = '测试我'
              this.$set(item1,'trueFalse','成功')
              this.$set(item1,'matched','精确匹配')
              this.$set(item1,'relativeTest','present')
              this.$set(item1,'relativeType','present')
              // alert(event.target.getAttribute('label'))
              if (item.trueFalse === '成功'){
                  var iii = this.$refs.btn[index].style.paddingLeft
                  console.log(index)
                  var num = iii.replace(/[^0-9]/ig,"")
                  var zuinum = parseInt(num)+20*index
                  // console.log(zuinum)
                  this.$refs.btn[index+1].style.paddingLeft = `${zuinum}px`
                  // console.log(document.querySelector(item).style.paddingLeft)
              }
              const item2 = {
                  targetType:'matchfal',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
              // console.log(item2.style.paddingLeft)
          }
          if(type == 'dimmatch'){
              this.$set(item1,'matched','全文模糊匹配')
              this.$set(item1,'trueFalse','成功')
              this.$set(item1,'cursorRegion','0')
              const item2 = {
                  targetType:'dimmatchfal',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
          }
          if(type == 'lipre'){
              this.$set(item1,'matched','按行精确匹配')
              this.$set(item1,'trueFalse','成功')
              this.$set(item1,'position',0)
              const item2 = {
                  targetType:'liprefal',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
          }
          if(type == 'dimpre'){
              this.$set(item1,'matched','按行模糊匹配')
              this.$set(item1,'trueFalse','成功')
              this.$set(item1,'position',0)
              const item2 = {
                  targetType:'dimprefal',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
          }
          if (type == 'analyse'){
              this.forms.dynamicItem.forEach(e=>{
                  if (e.targetType === 'takeword'){
                      if (e.hasOwnProperty('wordName') && e.wordName != ''){
                          this.biList.push(e.wordName)
                          let newbiList = []
                          for(let i = 0;i<this.biList.length;i++){
                              if (newbiList.indexOf(this.biList[i])==-1){
                                  newbiList.push(this.biList[i])
                              }
                          }
                          this.biList = newbiList
                      }
                  }
              })
              this.$set(item1,'action','比较')
              this.$set(item1,'trueFalse','成功')
              const item2 = {
                  targetType:'analysefal',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
          }
          if(type == 'takeword'){
              this.$set(item1,'action','取词')
              this.$set(item1,'position',0)
              this.$set(item1,'cursorRegion','0')
          }
          if (type == 'wloop'){
              this.$set(item1,'action','循环')
          }
          if (type == 'prodes'){
              this.$set(item1,'action','问题')
              this.$set(item1,'problemId',this.proId)
          }
          this.forms.dynamicItem.splice(thisIndex+1,0,item1)
      },
      //添加表单项
      addItem(type,item,index){
          let thisData = Date.now()
          console.log(thisData)
          let item1 = {
              targetType:type,
              onlyIndex:thisData,
              trueFalse:'',
              checked:false
          }
          let thisIndex = this.forms.dynamicItem.indexOf(item)
          console.log(thisIndex)
          if (type == 'command'){
              //默认校验方式：自定义校验
              this.$set(item1,'resultCheckId','0')
          }
          if(type == 'match'){
              this.$set(item1,'trueFalse','成功')
              this.$set(item1,'matched','精确匹配')
              this.$set(item1,'relativeTest','present')
              this.$set(item1,'relativeType','present')
              const item2 = {
                  targetType:'matchfal',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
          }
          if (type == 'analyse'){
              this.forms.dynamicItem.forEach(e=>{
                  if (e.targetType === 'takeword'){
                      if (e.hasOwnProperty('wordName') && e.wordName != ''){
                          this.biList.push(e.wordName)
                          let newbiList = []
                          for(let i = 0;i<this.biList.length;i++){
                              if (newbiList.indexOf(this.biList[i])==-1){
                                  newbiList.push(this.biList[i])
                              }
                          }
                          this.biList = newbiList
                      }
                  }
              })
              this.$set(item1,'action','比较')
              this.$set(item1,'trueFalse','成功')
              const item2 = {
                  targetType:'analysefal',
                  onlyIndex:thisData
              }
              this.$set(item2,'trueFalse','失败')
              this.forms.dynamicItem.splice(thisIndex+1,0,item2)
          }
          if(type == 'takeword'){
              this.$set(item1,'action','取词')
              this.$set(item1,'position',0)
              this.$set(item1,'cursorRegion','0')
          }
          if (type == 'wloop'){
              this.$set(item1,'action','循环')
          }
          if (type == 'prodes'){
              this.$set(item1,'action','问题')
              this.$set(item1,'problemId',this.proId)
          }
          this.forms.dynamicItem.splice(thisIndex+1,0,item1)
      },

      //删除
      deleteItem (item, index) {
          this.forms.dynamicItem.splice(index,1)
      },
      deleteItemp(item,index){
          this.forms.dynamicItem.splice(index,1)
          const shaIn = item.onlyIndex
          const shaAll = this.forms.dynamicItem.findIndex(it=>{
              if (it.onlyIndex === shaIn){
                  return true
              }
          })
          this.forms.dynamicItem.splice(shaAll,1)
      },
      // proname(){
      //     // console.log(item.prodes)
      //     var proNamess = ""
      //     this.forms.dynamicItem.map((item)=>{
      //         proNamess = item.prodes
      //     })
      //     alert(proNamess)
      // },
      //解决问题
      // jiejue(){
      //   this.$router.push('solve_question')
      //     var arr = []
      //     function getJson(key,jsonObj){
      //         for(var index in jsonObj){
      //             getJson1(key,jsonObj[index]);
      //         }
      //     }
      //     function getJson1(key,jsonObj){
      //         for (var p1 in jsonObj) {
      //             if(p1 === key){
      //                 console.log(jsonObj[key]);
      //                 arr.push(jsonObj[key])
      //             }else if(jsonObj[p1] instanceof Array) {
      //                 getJson(key,jsonObj[p1]);
      //             }
      //         }
      //     }
      //     getJson('wordName',this.forms.dynamicItem);
      //     alert(arr)
      // },

      //初次提交定义问题
      tijiao(){
          // this.forms.dynamicItem.shift(); hasOwnProperty(存在) 删除数组第一个元素
          const useForm = []
          const useLess = []
          this.forms.dynamicItem.forEach(e=>{
              if (e.test === "test"){
                  useLess.push(e)
              }else {
                  useForm.push(e)
              }
          })
          useForm.forEach(eeee=>{
              const thisIndex = useForm.indexOf(eeee)
              if(useForm.length != thisIndex+1){
                  const thisNext = useForm[thisIndex+1]
                  this.$set(eeee,'nextIndex',thisNext.onlyIndex)
              }
              if (eeee.action === '取词'){
                  eeee.length = `${eeee.length1}${eeee.classify}`
              }
              this.$set(eeee,'pageIndex',thisIndex+1)
              if (eeee.targetType == 'takeword'){
                  const takeWordt = useForm.indexOf(eeee)
                  var quciC = ''
                  useForm.map((e11)=>{
                      const takeWl = useForm.indexOf(e11)
                      if(takeWl == takeWordt-1){
                          quciC = e11.matchContent
                      }
                  })
                  this.$set(eeee,'matchContent',quciC)
              }
              //拆分有无问题
              if (eeee.action === '问题'){
                  const neid = eeee.action
                  console.log(neid)
                  const thisData = Date.now()
                  console.log(thisData)
              }
          })
          const handForm = useForm.map(x => JSON.stringify(x))
          console.log(handForm)
          console.log(this.proId)
          if (this.proId != ''){
              return request({
                  // url:'/sql/DefinitionProblemController/definitionProblemJsonPojo',
                  url:`/sql/DefinitionProblemController/definitionProblemJsonPojo?totalQuestionTableId=${this.proId}`,
                  method:'post',
                  data:handForm
              }).then(response=>{
                  console.log(response)
                  this.$message.success('提交成功!')
              })
          }else {
              this.$message.warning('未定义该问题，提交失败!')
          }

          // this.$router.go(0)   刷新页面
      },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        ip: null,
        name: null,
        type: null,
        password: null,
        end: null,
        port: null
      };
      this.resetForm("form");
    },


    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateJh_test1(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addJh_test1(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除多个扫描编号为"' + ids + '"的数据项？').then(function() {
        return delJh_test1(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$modal.confirm('是否确认导出所有多个扫描数据项？').then(() => {
        this.exportLoading = true;
        return exportJh_test1(queryParams);
      }).then(response => {
        this.$download.name(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
  .qqqqq{
    height: 60vh;
    overflow-x: hidden;
  }
  .el-form-item{
    margin-top: 5px;
    margin-bottom: 5px;
  }
  .el-dialog__header{
    padding: 10px;
  }
  .el-dialog__body{
    padding: 10px;
  }
  .redColor{
    color: red;
  }
  .context-menu-list{
    height: 20px;
  }
  .vue-contextmenu-listWrapper{
    height: 30px!important;
  }
  >>> .vue-contextmenu-listWrapper .context-menu-list{
    height: 22px!important;
  }
  >>> label{
    font-weight: normal;
  }
  >>> .strongW label{
    font-weight: 700;
    padding-right: 0;
  }
  >>> .blockW label{
    display: block;
    margin-right: 5px;
  }
  .contextmenu {
    margin: 0;
    background: #fff;
    z-index: 3000;
    position: absolute;
    list-style-type: none;
    padding: 5px 0;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 400;
    color: #333;
    box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, 0.3);
    position: fixed;
  }
  .contextmenu li {
    margin: 0;
    /*padding: 7px 16px;*/
    cursor: pointer;
  }
  .contextmenu li button{
    padding: 15px;
  }
  .contextmenu li:hover {
    background: #eee;
  }
</style>
