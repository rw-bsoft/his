<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cdh.schemas.CDH_OneYearSummary" alias="儿童周岁小结">
  <item id="phrId" pkey="true" alias="档案编号" type="string" length="30"
    not-null="1" fixed="true" colspan="2" queryable="true"/>
  <item id="manaUnitId" alias="管辖机构" type="string" length="20"
    fixed="true" colspan="2">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
  </item>
  <item id="growth" alias="生长发育情况" type="string" length="200"
    colspan="4" />
  <item id="intellectual" alias="智力发育情况" type="string" length="200"
    colspan="4" />
  <item id="nutritionStatus" alias="营养状况" type="string" length="200"
    colspan="4" >
    <dic render="LovCombo">
      <item key="1" text="正常" />
      <item key="2" text="超重" />
      <item key="3" text="轻度肥胖" />
      <item key="4" text="中度肥胖" />
      <item key="5" text="重度肥胖" />
      <item key="6" text="体重低下" />
      <item key="7" text="生长迟缓" />
      <item key="8" text="消瘦" />
      <item key="9" text="严重慢性营养不良" />
    </dic>
  </item>
  <item id="disease" alias="曾患疾病" type="string" length="200"
    colspan="4" />
  <item id="height" alias="身高(cm)" type="bigDecimal" length="5"
    precision="2" />
  <item id="weight" alias="体重(kg)" type="bigDecimal" length="5"
    precision="2" />
  <item id="headMeasurement" alias="头围(cm)" type="bigDecimal" length="5"
    precision="2" colspan="2" />
  <item id="pupil" alias="眼" type="string" length="50" >
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="眼睑浮肿" />
      <item key="3" text="眼睑下垂" />
      <item key="4" text="眼球突出" />
      <item key="5" text="结膜充血" />
      <item key="6" text="眼分泌物多" />
      <item key="7" text="角膜浑浊" />
      <item key="8" text="斜视" />
      <item key="9" text="砂眼" />
      <item key="10" text="其他" />
			
    </dic>
  </item>
  <item id="ear" alias="耳" type="string" length="50" >
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="耳廓畸形" />
      <item key="3" text="附耳" />
      <item key="4" text="耳前窦道" />
      <item key="5" text="外耳（道）湿疹" />
      <item key="6" text="叮咛堵塞" />
      <item key="7" text="其他" />
    </dic>
  </item>
  <item id="nose" alias="鼻" type="string" length="50" >
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="鼻腔分泌物" />
      <item key="3" text="鼻腔充血" />
      <item key="4" text="其他" />
    </dic>
  </item>
  <item id="teeth" alias="牙齿(颗)" type="int" />
  <item id="decayedTooth" alias="龋齿" type="string" length="50"
    display="0" >
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="heart" alias="心" type="string" length="50" >
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="心脏杂音Ⅰ" />
      <item key="3" text="心脏杂音Ⅱ" />
      <item key="4" text="心脏杂音Ⅲ" />
      <item key="5" text="心脏杂音Ⅳ" />
      <item key="6" text="心律不齐" />
      <item key="7" text="心率快" />
      <item key="8" text="其他" />
    </dic>
  </item>
  <item id="lung" alias="肺" type="string" length="50" >
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="呼吸音粗" />
      <item key="3" text="湿罗音" />
      <item key="4" text="干罗音" />
      <item key="5" text="哮鸣音" />
      <item key="6" text="呼吸增快" />
      <item key="7" text="其他" />
			
    </dic>
  </item>
  <item id="liver" alias="肝" type="string" length="50" >
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="肿大" />
    </dic>
  </item>
  <item id="spleen" alias="脾" type="string" length="50" >
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="肿大" />
    </dic>
  </item>
  <item id="extremities" alias="四肢" type="string" length="50" >
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="脊柱侧歪" />
      <item key="3" text="O(X)型腿" />
      <item key="4" text="手（脚）镯" />
      <item key="5" text="短小肢" />
      <item key="6" text="多指（趾）" />
      <item key="7" text="并指（趾）" />
      <item key="8" text="髋关节脱位" />
      <item key="9" text="马蹄内翻足" />
      <item key="10" text="双侧不对称" />
      <item key="11" text="肌张力增高" />
      <item key="12" text="松弛" />
      <item key="13" text="其他" />
    </dic>
  </item>
  <item id="genitals" alias="外生殖器" type="string" length="50" >
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="肛门闭锁" />
      <item key="3" text="无肛" />
      <item key="4" text="肛裂" />
      <item key="5" text="肛旁脓肿" />
      <item key="6" text="湿疹" />
      <item key="7" text="尿道下裂" />
      <item key="8" text="两性畸形" />
      <item key="9" text="隐睾" />
      <item key="10" text="鞘膜积液" />
      <item key="11" text="腹股沟疝" />
      <item key="12" text="其他" />
    </dic>
  </item>
  <item id="skin" alias="皮肤" type="string" length="50" >
    <dic>
      <item key="1" text="正常" />
      <item key="2" text="红润" />
      <item key="3" text="苍白" />
      <item key="4" text="黄染" />
      <item key="5" text="湿疹" />
      <item key="6" text="血管瘤" />
      <item key="7" text="其他" />
    </dic>
  </item>
  <item id="other" alias="其它" type="string" length="50" />
  <item id="hearingTest1" alias="听力筛查" type="string" length="50"
    colspan="4" >
    <dic dic="hearingTest1" render="LovCombo"/>
  </item>
  <item id="feedWay" alias="智力筛查" type="string" length="50"
    colspan="4" />
  <item id="others" alias="其它情况" type="string" length="200"
    colspan="4" />
  <item id="RBC2" alias="血色素(g/L)" type="bigDecimal" length="5"
    precision="2" colspan="4" />
  <item id="summaryUnit" alias="总评单位" type="string" length="20"
    defaultValue="%user.manageUnit.id" fixed="true" colspan="2" update="false">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="summaryDoctor" alias="总评者" type="string" length="20" update="false"
    defaultValue="%user.userId" colspan="2" queryable="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
    <set type="exp">['$','%user.userId']</set>
  </item>	
  <item id="summaryDate" alias="总评日期" type="datetime"  xtype="datefield" colspan="4" update="false"
    fixed="true" defaultValue="%server.date.today" >
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="generalComment" alias="总结评价" type="string"
    xtype="textarea" length="500" colspan="2" anchor="100%" display="0"/>
  <item id="guide" alias="指导" type="string" xtype="textarea"
    length="500" colspan="2" anchor="100%" display="0"/>
  <item id="memo" alias="备注" type="string" length="100" colspan="4" display="0"/>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="1">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"  display="1"
    width="180" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="1">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>
