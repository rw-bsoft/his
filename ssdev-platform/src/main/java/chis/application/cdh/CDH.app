<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.cdh.CDH" name="儿童档案"  type="1">
	<catagory id="CDH" name="儿童档案">
		<module id="H01" name="儿童档案管理" script="chis.script.CombinedDocList"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_HealthCard</p>  
          <p name="manageUnitField">a.manaUnitId</p>  
          <p name="areaGridField">a.homeAddress</p> 
        </properties>  
        <action id="list" name="列表视图" viewType="list" ref="chis.application.cdh.CDH/CDH/H0101"/> 
      </module>  
      <module id="H0101" name="儿童档案列表" type="1" script="chis.application.cdh.script.record.ChildrenHealthRecordList"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_HealthCard</p>  
          <p name="saveServiceId">chis.childrenHealthRecordService</p>  
        </properties>  
        <action id="createByEmpi" name="新建" iconCls="create"/>  
        <action id="modify" name="查看" iconCls="update"/>  
        <action id="writeOff" name="注销" iconCls="remove" ref="chis.application.cdh.CDH/CDH/H0102"/>  
        <action id="endManage" name="结案" iconCls="close"/>  
        <action id="print" name="打印"/> 
      </module>  
      <module id="H0102" name="儿童档案注销表单" type="1" script="chis.application.cdh.script.record.ChildrenHealthRecordWriteOffForm"> 
        <properties> 
          <p name="entryName">chis.application.pub.schemas.EHR_WriteOff</p>  
        </properties>  
        <action id="save" name="确定"/>  
        <action id="cancel" name="取消" iconCls="common_cancel"/> 
      </module>  
      <module id="H02" name="出生医学证明" script="chis.script.CombinedDocList"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_BirthCertificate</p>  
          <p name="manageUnitField">a.createUnit</p>  
          <p name="areaGridField">d.regionCode</p> 
        </properties>  
        <action id="list" name="列表视图" viewType="list" ref="chis.application.cdh.CDH/CDH/H02_1"/> 
      </module>  
      <module id="H02_1" name="出生医学证明列表" type="1" script="chis.application.cdh.script.birthCertificate.BirthCertificateList"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_BirthCertificate</p>  
          <p name="refModule">chis.application.cdh.CDH/CDH/H02_2</p>  
          <p name="saveServiceId">chis.birthCertificateService</p> 
        </properties>  
        <action id="createByEmpi" name="新建" iconCls="create"/>  
        <action id="modify" name="查看" iconCls="update"/>  
        <action id="remove" name="删除" iconCls="remove"/> 
      </module>  
      <module id="H02_2" name="出生医学证明" type="1" script="chis.application.cdh.script.birthCertificate.BirthCertificateForm"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_BirthCertificate</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="保存" group="update"/> 
      </module>  
      <module id="H03" name="儿童随访计划列表" type="1" script="chis.application.cdh.script.ChildrenVisitPlanListView"> 
        <properties> 
          <p name="entryName">chis.application.pub.schemas.PUB_VisitPlan_Children</p> 
        </properties> 
      </module>  
      <module id="H0111_7" name="疑似残疾儿童报告卡" type="1" script="chis.application.cdh.script.disability.DisabilityMonitorRecordForm"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DisabilityMonitor</p>  
          <p name="saveServiceId">chis.DisabilityMonitorService</p>  
          <p name="saveAction">saveDisabilityMonitorRecord</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="update"/>  
        <action id="print" name="打印"/> 
      </module>  
      <module id="H0111_1" name="儿童档案基本信息表单" type="1" script="chis.application.cdh.script.record.ChildrenHealthRecordForm"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_HealthCard</p>  
          <p name="saveServiceId">chis.childrenHealthRecordService</p>  
          <p name="saveAction">saveChildrenRecord</p>  
          <p name="loadServiceId">chis.childrenHealthRecordService</p>  
          <p name="loadAction">getChildHealthCard</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="create||update"/> 
      </module>  
      <module id="H0111_2" name="儿童询问记录整体模块" type="1" script="chis.application.cdh.script.inquire.ChildrenInquireModule"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_Inquire</p>  
          <p name="saveServiceId">chis.childrenHealthRecordService</p> 
        </properties>  
        <action id="InquirePlanList" name="儿童询问计划列表" ref="chis.application.cdh.CDH/CDH/H03"/>  
        <action id="InquireForm" name="儿童询问记录表单" ref="chis.application.cdh.CDH/CDH/H0111_2_1"/> 
      </module>  
      <module id="H0111_2_1" name="儿童询问记录表单" type="1" script="chis.application.cdh.script.inquire.ChildrenInquireForm"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_Inquire</p>  
          <p name="saveAction">saveInquireRecord</p>  
          <p name="saveServiceId">chis.childrenHealthRecordService</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="update"/> 
      </module>  
      <module id="H0111_3" name="儿童意外情况整体模块" type="1" script="chis.application.cdh.script.accident.ChildrenAccidentModule"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_Accident</p>  
          <p name="removeServiceId">chis.simpleRemove</p>  
          <p name="saveServiceId">chis.childrenHealthRecordService</p>  
          <p name="saveAction">saveAccidentRecord</p> 
        </properties>  
        <action id="AccidentList" name="儿童意外情况列表" ref="chis.application.cdh.CDH/CDH/H0111_3_1"/>  
        <action id="AccidentForm" name="儿童意外情况表单" ref="chis.application.cdh.CDH/CDH/H0111_3_2"/> 
      </module>  
      <module id="H0111_3_1" name="儿童意外情况列表" type="1" script="chis.application.cdh.script.accident.ChildrenAccidentList"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_Accident</p> 
        </properties> 
      </module>  
      <module id="H0111_3_2" name="儿童意外情况表单" type="1" script="chis.application.cdh.script.accident.ChildrenAccidentForm"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_Accident</p>  
          <p name="saveAction">saveAccidentRecord</p>  
          <p name="saveServiceId">chis.childrenHealthRecordService</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="update"/>  
        <action id="create" name="新建" iconCls="create" group="update"/>  
        <action id="remove" name="删除" group="update"/>  
        <action id="print" name="打印"/> 
      </module>  
      <module id="H0111_6" name="儿童三周岁小结表单" type="1" script="chis.application.cdh.script.summary.ChildrenOneYearSummaryForm"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_OneYearSummary</p>  
          <p name="saveServiceId">chis.childrenOneYearSummaryService</p>  
          <p name="saveAction">saveChildrenOneYearSummaryRecord</p> 
        </properties>  
        <action id="save" name="确定" group="update"/>  
        <action id="create" name="自动生成" iconCls="post" group="update"/> 
      </module>  
      <module id="H04" name="儿童出生缺陷监测" script="chis.script.CombinedDocList"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DefectRegister</p>  
          <p name="manageUnitField">a.manaUnitId</p>  
          <p name="areaGridField">d.homeAddress</p> 
        </properties>  
        <action id="list" name="列表视图" viewType="list" ref="chis.application.cdh.CDH/CDH/H0401"/> 
      </module>  
      <module id="H0401" name="儿童出生缺陷监测列表" script="chis.application.cdh.script.defect.ChildrenDefectList" type="1" icon="default"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DefectRegister</p>  
          <p name="saveServiceId">chis.childrenHealthRecordService</p>  
          <p name="removeServiceId">chis.childrenHealthRecordService</p>  
          <p name="saveAction">saveDefectChildrenRecord</p>  
          <p name="removeAction">deleteDefectChildrenRecord</p>  
        </properties>  
        <action id="createDefect" name="新建" iconCls="create"/>  
        <action id="modify" name="查看" iconCls="update"/>  
        <action id="remove" name="删除"/>  
        <action id="print" name="打印"/> 
      </module>  
      <module id="H0401_1" name="儿童出生缺陷监测表单" type="1" script="chis.application.cdh.script.defect.ChildrenDefectRecordForm"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DefectRegister</p>  
          <p name="saveServiceId">chis.childrenHealthRecordService</p>  
          <p name="saveAction">saveDefectChildrenRecord</p>  
          <p name="loadServiceId">chis.childrenHealthRecordService</p>  
          <p name="loadAction">getChildDefectRecord</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="update"/> 
      </module>  
      <module id="H05" name="儿童死亡监测" script="chis.script.CombinedDocList"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DeadRegister</p>  
          <p name="manageUnitField">a.manaUnitId</p>  
          <p name="areaGridField">a.homeAddress</p> 
        </properties>  
        <action id="list" name="列表视图" viewType="list" ref="chis.application.cdh.CDH/CDH/H0501"/> 
      </module>  
      <module id="H0501" name="儿童死亡监测列表" type="1" script="chis.application.cdh.script.dead.ChildrenDeadList"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DeadRegister</p>  
          <p name="saveServiceId">chis.childrenHealthRecordService</p>  
          <p name="removeServiceId">chis.childrenHealthRecordService</p>  
          <p name="removeAction">removeChildrenDeadRecord</p>  
          <p name="recordCreateRef">chis.application.cdh.CDH/CDH/H0501_01</p>  
          <p name="noRecordCreateRef">chis.application.cdh.CDH/CDH/H0501_02</p> 
        </properties>  
        <action id="createDead" name="新建" iconCls="create"/>  
        <action id="modify" name="查看" iconCls="update"/>  
        <action id="remove" name="删除"/>  
        <action id="createNh" name="无档新建" iconCls="create"/>  
        <action id="print" name="打印"/> 
      </module>  
      <module id="H0501_01" name="儿童死亡登记表单" type="1" script="chis.application.cdh.script.dead.ChildrenDeadRegisterForm"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DeadRegister</p>  
          <p name="saveServiceId">chis.childrenHealthRecordService</p>  
          <p name="saveAction">saveChildrenDeadRecord</p> 
        </properties>  
        <action id="save" name="确定" group="create||update"/>  
        <action id="print" name="打印"/>  
        <action id="cancel" name="取消" iconCls="common_cancel"/> 
      </module>  
      <module id="H0501_02" name="儿童无档死亡登记表单" type="1" script="chis.application.cdh.script.dead.ChildrenDeadRegisterFormNh"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DeadRegisterNh</p>  
          <p name="saveServiceId">chis.childrenHealthRecordService</p>  
          <p name="saveAction">saveChildrenDeadRecordNH</p> 
        </properties>  
        <action id="save" name="确定" group="create"/>  
        <action id="cancel" name="取消" iconCls="common_cancel"/> 
      </module>  
      <module id="H97" name="1岁以内儿童体格检查表单整体模块" script="chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneModule" type="1"> 
        <properties> 
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="ChildrenCheckupPlanList" name="计划列表" ref="chis.application.cdh.CDH/CDH/H03"/>  
        <action id="ChildrenCheckupInOneForm" name="1岁以内儿童体检" ref="chis.application.cdh.CDH/CDH/H97-1" type="tab"/>  
        <action id="ChildrenHealthGuidanceForm" name="健康指导" ref="chis.application.cdh.CDH/CDH/H13-1" type="tab"/>   
        <action id="ChildrenCheckupDescriptionForm" name="中医辩体" ref="chis.application.cdh.CDH/CDH/H97-2" type="tab"/>  
        <action id="ChildrenCheckupHealthTeachForm" name="健康教育" ref="chis.application.cdh.CDH/CDH/H97-3" type="tab"/> 
      </module>  
      <module id="H98" name="1-2岁儿童体格检查表单整体模块" script="chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoModule" type="1"> 
        <properties> 
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="ChildrenCheckupPlanList" name="计划列表" ref="chis.application.cdh.CDH/CDH/H03"/>  
        <action id="ChildrenCheckupOneToTwoForm" name="1-2岁儿童体检" ref="chis.application.cdh.CDH/CDH/H98-1" type="tab"/>
        <action id="ChildrenHealthGuidanceForm" name="健康指导" ref="chis.application.cdh.CDH/CDH/H13-1" type="tab"/>   
        <action id="ChildrenCheckupDescriptionForm" name="中医辩体" ref="chis.application.cdh.CDH/CDH/H97-2" type="tab"/>  
        <action id="ChildrenCheckupHealthTeachForm" name="健康教育" ref="chis.application.cdh.CDH/CDH/H97-3" type="tab"/> 
      </module>  
      <module id="H99" name="3-6岁儿童体格检查表单整体模块" script="chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixModule" type="1"> 
        <properties> 
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="ChildrenCheckupPlanList" name="计划列表" ref="chis.application.cdh.CDH/CDH/H03"/>  
        <action id="ChildrenCheckupThreeToSixForm" name="3-6岁儿童体检" ref="chis.application.cdh.CDH/CDH/H99-1" type="tab"/>  
        <action id="ChildrenHealthGuidanceForm" name="健康指导" ref="chis.application.cdh.CDH/CDH/H13-1" type="tab"/>   
        <action id="ChildrenCheckupDescriptionForm" name="中医辩体" ref="chis.application.cdh.CDH/CDH/H97-2" type="tab"/>  
        <action id="ChildrenCheckupHealthTeachForm" name="健康教育" ref="chis.application.cdh.CDH/CDH/H97-3" type="tab"/> 
      </module>  
      <module id="H97-1" name="儿童体检" script="chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneForm" type="1"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_CheckupInOne</p>  
          <p name="saveServiceId">chis.childrenCheckupService</p>  
          <p name="saveAction">saveChildCheckup</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="update"/> 
      </module>  
      <module id="H97-1-1" name="儿童体检" script="chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneHtmlForm" type="1"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_CheckupInOne</p>  
          <p name="saveServiceId">chis.childrenCheckupService</p>  
          <p name="saveAction">saveChildCheckup</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="update"/> 
      </module>  
      <module id="H98-1" name="儿童体检" script="chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoForm" type="1"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_CheckupOneToTwo</p>  
          <p name="saveServiceId">chis.childrenCheckupService</p>  
          <p name="saveAction">saveChildCheckup</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="update"/> 
      </module>  
       <module id="H98-1-1" name="儿童体检" script="chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoHtmlForm" type="1"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_CheckupOneToTwo</p>  
          <p name="saveServiceId">chis.childrenCheckupService</p>  
          <p name="saveAction">saveChildCheckup</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="update"/> 
      </module>  
      <module id="H99-1" name="儿童体检" script="chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixForm" type="1"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_CheckupThreeToSix</p>  
          <p name="saveServiceId">chis.childrenCheckupService</p>  
          <p name="saveAction">saveChildCheckup</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="update"/> 
      </module>  
       <module id="H99-1-1" name="儿童体检" script="chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixHtmlForm" type="1"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_CheckupThreeToSixPaper</p>  
          <p name="saveServiceId">chis.childrenCheckupService</p>  
          <p name="saveAction">saveChildCheckup</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="update"/> 
      </module>  
      <module id="H13-1"  type="1"  name="健康指导" script="chis.application.cdh.script.checkup.ChildrenHealthGuidanceForm"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_HealthGuidance</p>  
          <p name="saveServiceId">chis.childrenCheckupService</p>  
          <p name="saveAction">saveHealthGuidance</p>  
          <p name="loadServiceId">chis.childrenCheckupService</p>  
          <p name="loadAction">getHealthGuidance</p> 
        </properties>  
        <action id="save" name="确定" group="update"/> 
      </module>  
      <module id="H97-2" name="中医辩体" script="chis.application.cdh.script.checkup.ChildrenCheckupDescriptionForm" type="1"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_ChildrenCheckupDescription</p>  
          <p name="saveServiceId">chis.childrenCheckupService</p>  
          <p name="saveAction">saveCheckupDescription</p>  
          <p name="loadServiceId">chis.childrenCheckupService</p>  
          <p name="loadAction">getCheckupDescription</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="update"/> 
      </module>  
      <module id="H97-3" type="1" name="健康教育" script="chis.application.cdh.script.checkup.ChildrenCheckupHealthTeachForm"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_CheckupHealthTeach</p>  
          <p name="saveServiceId">chis.childrenCheckupService</p>  
          <p name="saveAction">saveCheckupHealthTeach</p>  
          <p name="loadServiceId">chis.childrenCheckupService</p>  
          <p name="loadAction">getCheckupHealthTeach</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="update"/> 
      </module>  
      <module id="H09" name="体弱儿童档案管理" script="chis.script.CombinedDocList"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DebilityChildren</p>  
          <p name="manageUnitField">d.manaUnitId</p>  
          <p name="areaGridField">d.homeAddress</p> 
        </properties>  
        <action id="list" name="列表视图" viewType="list" ref="chis.application.cdh.CDH/CDH/H0901"/> 
      </module>  
      <module id="H0901" name="体弱儿童档案列表" type="1" script="chis.application.cdh.script.debility.record.DebilityChildrenRecordList"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DebilityChildren</p>  
          <p name="ServiceId">chis.debilityChildrenService</p> 
        </properties>  
        <action id="createByEmpi" name="新建" iconCls="create"/>  
        <action id="modify" name="查看" iconCls="update"/>  
        <action id="writeOff" name="注销" iconCls="remove" ref="chis.application.cdh.CDH/CDH/H0902"/>  
        <action id="print" name="打印"/> 
      </module>  
      <module id="H0902" name="体弱儿档案注销表单" type="1" script="chis.application.cdh.script.debility.record.DebilityChildrenRecordWriteOffForm"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DebilityChildrenLogout</p>  
        </properties>  
        <action id="save" name="确定" group="update"/>  
        <action id="cancel" name="取消" iconCls="common_cancel"/> 
      </module>  
      <module id="H0901-1" name="体弱儿童档案表单" script="chis.application.cdh.script.debility.record.DebilityChildrenRecordForm" type="1"> 
        <properties> 
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="create||update"/>  
        <action id="create" name="新建" group="create"/>  
        <action id="close" name="结案" group="close"/>  
        <action id="print" name="打印" iconCls="print"/> 
      </module>  
      <module id="H0901-2" name="体弱儿童档案列表" script="chis.application.cdh.script.debility.record.DebilityChildrenModuleList" type="1"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DebilityChildren_Module</p>  
          <p name="serverParams" type="jo">{"actions":"update,create,close"}</p> 
        </properties> 
      </module>  
      <module id="H09-1" name="体弱儿童档案表单" description="体弱儿档案模块" script="chis.application.cdh.script.debility.record.DebilityChildrenRecordModule" type="1"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DebilityChildren</p>  
          <p name="saveServiceId">chis.debilityChildrenService</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="DebilityRecordList" name="体弱儿档案列表" ref="chis.application.cdh.CDH/CDH/H0901-2"/>  
        <action id="DebilityRecordForm" name="体弱儿档案" ref="chis.application.cdh.CDH/CDH/H0901-1" type="tab"/>  
        <action id="DebilityVisitForm" name="体弱儿随访" ref="chis.application.cdh.CDH/CDH/H09-2" type="tab"/> 
      </module>  
      <module id="H09-2" name="体弱儿童随访整体模块" description="随访整体" script="chis.application.cdh.script.debility.visit.DebilityChildrenVisitModule" type="1"> 
        <properties> 
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="VisitList" name="随访计划" ref="chis.application.cdh.CDH/CDH/H09-2-1"/>  
        <action id="VisitFormView" name="随访基本信息" ref="chis.application.cdh.CDH/CDH/H09-2-2-1" type="tab"/>  
        <action id="CheckListView" name="化验项目" ref="chis.application.cdh.CDH/CDH/H09-2-2-2" type="tab"/> 
      </module>  
      <module id="H09-2-1" name="随访计划" script="chis.application.cdh.script.debility.visit.DebilityChildrenVisitList" type="1"> 
        <properties> 
          <p name="entryName">chis.application.pub.schemas.PUB_VisitPlan</p> 
        </properties> 
      </module>  
      <module id="H09-2-2-1" name="随访基本信息" script="chis.application.cdh.script.debility.visit.DebilityChildrenVisitForm" type="1"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DebilityChildrenVisit</p>  
          <p name="saveServiceId">chis.debilityChildrenService</p>  
          <p name="saveAction">saveDebilityChildrenVisit</p>  
          <p name="isAutoScroll">true</p> 
        </properties>  
        <action id="save" name="确定" group="update"/>  
        <action id="print" name="打印" iconCls="print"/> 
      </module>  
      <module id="H09-2-2-2" name="化验项目列表" type="1" script="chis.application.cdh.script.debility.visit.DebilityChildrenCheckList"> 
        <properties> 
          <p name="entryName">chis.application.cdh.schemas.CDH_DebilityChildrenCheck</p>  
          <p name="saveAction">saveDebilityChildrenVisitCheck</p>  
          <p name="saveServiceId">chis.debilityChildrenService</p> 
        </properties>  
        <action id="save" name="确定" group="update"/> 
      </module>  
      <module id="H12" name="WHO标准儿童年龄别体重曲线图" script="chis.script.gis.powerChartView" type="1"> 
        <properties> 
          <p name="entryName">chis.report.CDH_kgline</p>  
        </properties> 
      </module>  
      <module id="H13" name="WHO标准儿童年龄别身长曲线图" script="chis.script.gis.powerChartView" type="1"> 
        <properties> 
          <p name="entryName">chis.report.CDH_cmline</p>  
        </properties> 
      </module>  
      <module id="H14" name="9市标准儿童年龄别体重身长曲线图" script="chis.script.gis.powerChartView" type="1"> 
        <properties> 
          <p name="entryName">chis.report.94CDH_kgline</p>  
        </properties> 
      </module>  
      <module id="H15" name="9市标准儿童年龄别身长曲线图" script="chis.script.gis.powerChartView" type="1"> 
        <properties> 
          <p name="entryName">chis.report.94CDH_cmline</p>  
        </properties> 
      </module> 
		<!--添加新生儿访视模块-->
		<module id="H02_17" name="新生儿访视" script="chis.application.cdh.script.newBorn.NewBornRecordModule" type="1"> 
			<action id="action1" name="新生儿访视列表" ref="chis.application.cdh.CDH/CDH/H02_17_3"/>  
			<action id="action2" name="新生儿访视表单" ref="chis.application.cdh.CDH/CDH/H02_17_2" />
		</module>
		<module id="H02_17_2" type="1" name="新生儿访视表单" script="chis.application.cdh.script.newBorn.NewBornRecordForm" > 
			<properties> 
				<p name="entryName">chis.application.cdh.schemas.CDH_ChildVisitInfoAndRecord</p>    
				<p name="refModule">chis.application.cdh.CDH/CDH/H02_17_2_1</p>  
			</properties>
			<action id="save" name="确定" iconCls="update"/>
			<action id="create" name="新建" iconCls="create"/>
			<action id="importIn" name="调入" iconCls="update"/>
			<action id="printNewBorn" name="打印" iconCls="update"/>
			
		</module>
		<module id="H02_17_3" name="儿童随访信息" type="1" script="chis.application.cdh.script.ChildrenVisitRecordListView"> 
			<properties> 
				<p name="entryName">chis.application.cdh.schemas.CDH_ChildVisitRecord_text</p> 
			</properties> 
		
 </module>  
		<module id="H02_17_2_1" type="1" name="新生儿访视的信息" script="chis.application.cdh.script.newBorn.ToMHCRecordModule" > 
			<action id="action1" name="新生儿访视基本信息列表" ref="chis.application.cdh.CDH/CDH/H02_17_2_1_1"/>  
			<action id="action2" name="新生儿访视记录列表" ref="chis.application.cdh.CDH/CDH/H02_17_2_1_2" />
		</module>
		<module id="H02_17_2_1_1" name="新生儿访视基本信息列表" type="1" script="chis.application.cdh.script.newBorn.ToMHCBabyVisitInfoListView"> 
			<properties> 
				<p name="entryName">chis.application.cdh.schemas.CDH_ChildVisitInfo_i</p> 
			</properties> 
			
		</module>  
		<module id="H02_17_2_1_2" name="新生儿访视记录列表" type="1" script="chis.application.cdh.script.newBorn.ToMHCBabyVisitRecordListView"> 
			<properties> 
				<p name="entryName">chis.application.cdh.schemas.CDH_ChildVisitRecord_r</p> 
			</properties> 
			<action id="importIn" name="调入" iconCls="update"/>
		</module>  
	</catagory>
</application>