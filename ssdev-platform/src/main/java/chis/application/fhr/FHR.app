<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.fhr.FHR" name="家庭档案"  type="1">
	<catagory id="FHR" name="家庭档案">
		<module id="B01" name="家庭档案管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord</p>
				<p name="navField">RegionCode</p>
				<p name="navDic">chis.@manageUnit</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.fhr.FHR/FHR/B01_" />
		</module>
		<module id="B01_" name="家庭档案列表"
			script="chis.application.fhr.script.FamilyRecordList" type="1">
			<properties>
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord</p>
				<p name="saveServiceId">chis.familyRecordService</p>
				<p name="listServiceId">chis.familyListQuery</p>
				<p name="removeServiceId">chis.familyRecordService</p>
				<p name="removeAction">removeFamilyRecord</p>
				<p name="refId">chis.application.fhr.FHR/FHR/B011</p>
			</properties>
			<action id="createFHR" name="新建" iconCls="create" />
			<action id="modify" name="查看" iconCls="update"/>
			<action id="remove" name="删除" />
			<action id="print" name="打印" />
			<!--<action id="sign" name="签约" iconCls="update"/>-->
		</module>
		<module id="B011" name="家庭档案整体模块" 
			script="chis.application.fhr.script.FamilyRecordModule" type="1">
			<properties>
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord</p>
				<p name="saveServiceId">chis.familyRecordService</p>
				<p name="saveAction">saveFamilyRecord</p>
			</properties>
			<action id="doc" name="基本信息" ref="chis.application.fhr.FHR/FHR/B011_1" type="tab" />
			<action id="members" name="成员列表" ref="chis.application.fhr.FHR/FHR/B011_2" type="tab" />
			<action id="problems" name="问题列表" ref="chis.application.fhr.FHR/FHR/B011_3" type="tab" />
			<!--新增家医签约、签约服务 替换原来的模块   Wangjl   2018-09-18 -->
			<!--<action id="contract" name="家庭签约" ref="chis.application.fhr.FHR/FHR/B011_5" type="tab"/>
			<action id="service" name="签约服务" ref="chis.application.fhr.FHR/FHR/B011_6" type="tab"/>-->
			<!--<action id="contract" name="家庭签约" ref="chis.application.fhr.FHR/FHR/B011_5" type="tab"/>
			<action id="service" name="服务记录" ref="chis.application.fhr.FHR/FHR/B011_6" type="tab"/> -->
		</module>
		<module id="B011_1" name="家庭基本信息表单" script="chis.application.fhr.script.FamilyRecordForm" type="1">
			<properties>
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord</p>
				<p name="saveServiceId">chis.familyRecordService</p>
				<p name="saveAction">saveFamilyRecord</p>
				<p name="loadServiceId">chis.familyRecordService</p>
				<p name="loadAction">loadFamilyRecord</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
		</module>
		<module id="B011_2" name="家庭成员列表" script="chis.application.fhr.script.FamilyMemberList"
			type="1">
			<properties>
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyMemberList</p>
				<p name="saveServiceId">chis.familyRecordService</p>
				<p name="removeServiceId">chis.familyRecordService</p>
				<p name="removeAction">removeFamilyNumber</p>
			</properties>
			<action id="readMember" name="详细信息" iconCls="read" />
			<action id="autoMatch" name="自动匹配" iconCls="healthDoc_autoMatch"
				group="update" ref="chis.application.fhr.FHR/FHR/B011_2_1" />
			<action id="remove" name="解除匹配" group="update"  />
			<action id="print" name="打印" />
		</module>
		<module id="B011_2_1" name="自动匹配家庭成员" script="chis.application.fhr.script.AutoMatchList"
			type="1">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_HealthRecord</p>
				<p name="saveServiceId">chis.familyRecordService</p>
				<p name="saveAction">batchMatchFamilyNumbers</p>
			</properties>
			<action id="confirmSelect" name="确定" iconCls="read" />
			<action id="showOnlySelected" name="查看已选" iconCls="update" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="B011_3" name="家庭主要问题列表" script="chis.application.fhr.script.FamilyProblemList"
			type="1">
			<properties>
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyProblem</p>
				<p name="saveServiceId">chis.familyRecordService</p>
			</properties>
			<action id="create" name="新建" ref="chis.application.fhr.FHR/FHR/B011_3_1" group="update" />
			<action id="update" name="查看" ref="chis.application.fhr.FHR/FHR/B011_3_1" />
			<action id="remove" name="删除" group="update" />
			<action id="print" name="打印" />
		</module>
		<module id="B011_3_1" name="家庭主要问题记录表单" script="chis.application.fhr.script.FamilyProblemForm"
			type="1">
			<properties>
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyProblem</p>
				<p name="saveServiceId">chis.familyRecordService</p>
				<p name="saveAction">saveFamilyProblem</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<!--新增家医签约、签约服务 替换原来的模块   Wangjl   2018-09-18 -->
		<module id="B011_5" name="家医签约" 
				script="chis.application.fhr.script.FamilyContractBase_JYQY" type="1">
		</module>
		<module id="B011_6" name="签约服务"  
			script="chis.application.fhr.script.FamilyContractBase_QYFW" type="1">
		</module>
		<!--<module id="B011_5" name="家庭签约信息管理" script="chis.application.fhr.script.FamilyContractBase" type="1"> 
			<properties> 
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyContractBase</p>
			</properties>
			<action id="action1" name="签约列表" ref="chis.application.fhr.FHR/FHR/B011_51" />
			<action id="action2" name="签约表单" ref="chis.application.fhr.FHR/FHR/B011_5_1_1" />
		</module>
		<module id="B011_51" name="家庭签约信息列表" script="chis.application.fhr.script.FamilyContractBaseList" type="1"> 
			<properties> 
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyContractBase</p>       
			</properties> 
			<action id="new" name="新建签约"/>
			<action id="remove" name="删除签约" />			
		</module>  
		<module id="B011_5_1_1" name="签约内容" script="chis.application.fhr.script.FamilyContractBaseModule" type="1"> 
			<properties> 
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyContractBase</p> 
			</properties>
			<action id="save" name="保存" iconCls="save"/>
			<action id="contactCard" name="联系卡" iconCls="print"/>
			<action id="print" name="协议书" iconCls="print"/>	
		</module>
		<module id="B011_6" name="服务记录" script="chis.application.fhr.script.FamilyServiceRecordModule" type="1"> 
			<action id="action1" name="家庭成员" ref="chis.application.fhr.FHR/FHR/B011_6_1" />
			<action id="action2" name="服务记录" ref="chis.application.fhr.FHR/FHR/B011_6_2" />
		</module>
		<module id="B011_6_1" name="家庭成员列表" script="chis.application.fhr.script.FamilyServiceRecordMemberList" type="1"> 
			<properties> 
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyMember</p>       
			</properties> 
		</module>
		<module id="B011_6_2" name="服务记录列表" script="chis.application.fhr.script.FamilyServiceRecordList" type="1"> 
			<properties> 
				<p name="entryName">chis.application.fhr.schemas.EHR_MasterplateData</p>       
			</properties> 
			<action id="createRecord" name="新建" iconCls="create"/>
			<action id="modify" name="查看" iconCls="update"/>
			<action id="remove" name="删除"/>
		</module>-->
		<module id="B011_7" name="个人签约" script="chis.application.fhr.script.PersonnalContractServiceList" type="1"> 
			<properties> 
				<p name="entryName">chis.application.fhr.schemas.EHR_PersonContractService</p> 
			</properties>
			<action id="create" name="新增签约" iconCls="add"/>	
			<action id="save" name="保存服务内容" iconCls="save"/>	
			<action id="remove" name="删除签约" />		
		</module>
		<module id="B011_9" name="服务项目字典维护"  type="1" script="chis.script.BizSimpleListView">
			<properties>
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyServicesItemDic</p> 
				<p name="saveServiceId">chis.simpleSave</p> 
				<p name="loadServiceId">chis.simpleLoad</p> 
			</properties>
			<action id="create" name="新建"/>
			<action id="update" name="查看"/>
			<action id="remove" name="删除"/>
		</module>
		<module id="B011_10" name="家庭成员迁移表单"  type="1" script="chis.application.fhr.script.MovRecordForm"> 
			<properties> 
				<p name="entryName">chis.application.fhr.schemas.EHR_Record</p> 
			</properties>  
			<action id="save" name="确定" group="update"/>  
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module>  
		<!--部分网格化地址-->
		<module id="B0110" name="家庭档案整体模块" 
			script="chis.application.fhr.script.FamilyRecordModule" type="1">
			<properties>                                         
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord</p>
				<p name="saveServiceId">chis.familyRecordService</p>
				<p name="saveAction">saveFamilyRecord</p>
			</properties>
			<action id="doc" name="基本信息" ref="chis.application.fhr.FHR/FHR/B0110_1" type="tab" />
			<action id="members" name="成员列表" ref="chis.application.fhr.FHR/FHR/B0110_2" type="tab" />
			<action id="moveRecords" name="迁移记录" ref="chis.application.fhr.FHR/FHR/B0110_7" type="tab" />
			<action id="followUp" name="随访任务" ref="chis.application.fhr.FHR/FHR/B0110_8" type="tab" />
			<action id="problems" name="问题列表" ref="chis.application.fhr.FHR/FHR/B011_3" type="tab" />
			<!--<action id="contract" name="家庭签约" ref="chis.application.fhr.FHR/FHR/B011_5" type="tab"/>
			<action id="service" name="服务记录" ref="chis.application.fhr.FHR/FHR/B011_6" type="tab"/> -->
		</module>
		<module id="B0110_1" name="家庭基本信息表单" script="chis.application.fhr.script.FamilyRecordForm1" type="1">
			<properties>
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord1</p>
				<p name="saveServiceId">chis.familyRecordService1</p>
				<p name="saveAction">saveFamilyRecord</p>
				<p name="loadServiceId">chis.familyRecordService1</p>
				<p name="loadAction">loadFamilyRecord</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
		</module>
		<module id="B0110_2" name="家庭成员列表" script="chis.application.fhr.script.FamilyMemberList1"
			type="1">
			<properties>
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyMemberList</p>
				<p name="saveServiceId">chis.familyRecordService1</p>
				<p name="removeServiceId">chis.familyRecordService1</p>
				<p name="removeAction">removeFamilyNumber</p>
			</properties>
			<action id="readMember" name="家庭关系" iconCls="read" />
			<action id="autoMatch" name="添加" iconCls="healthDoc_autoMatch" group="update" />
			<action id="removal" name="迁出" iconCls="healthDoc_autoMatch" group="update" />
			<action id="remove" name="删除成员" iconCls="update" group="update"/>
			<action id="sign" name="签约" iconCls="update"/>
			<action id="print" name="打印" />
		</module>
		<module id="B0110_7" name="迁移记录" script="chis.application.fhr.script.MoveRecordsList"
			type="1">
			<properties>.
				<p name="entryName"> chis.application.fhr.schemas.EHR_Record</p>
			</properties>
		</module>
		<module id="B0110_8" name="随访任务" script="chis.application.fhr.script.FollowUpList"
			type="1">
			<properties>
				<p name="entryName">chis.application.fhr.schemas.PUB_VisitPlanFamilyList</p>
				<p name="listServiceId">chis.familyRecordService1</p>
				<p name="listAction">loadFamilyVisitPlans</p>
			</properties>
			<action id="updateInfo" name="随访" iconCls="healthDoc_autoMatch" group="update" />
		</module>
	  <module id="QYFW" name="乡村签约服务" script="chis.script.CombinedDocList"> 
      <properties> 
        <p name="entryName">chis.application.fhr.schemas.PER_XCQYFW</p>  
        <p name="manageUnitField">a.manaUnitId</p>  
        <p name="areaGridField">c.regionCode</p>  
        <p name="navDic">chis.@manageUnit</p>  
        <p name="navField">chis.@manaUnitId</p> 
      </properties>  
      <action id="list" name="列表视图" viewType="list" ref="chis.application.fhr.FHR/FHR/QYFW_01"/> 
    </module>  
    <module id="QYFW_01" name="乡村签约服务模块" type="1" script="chis.application.fhr.script.PremaritalCheckFemaleListView"> 
      <properties>
        <arg name="entryName">chis.application.fhr.schemas.PER_XCQYFW</arg>  
        <arg name="navField">chis.@manaUnitId</arg>  
        <arg name="navDic">chis.@manageUnit</arg>  
        <arg name="winState" type="jo">{pos:[0,0]}</arg>  
      </properties>
      <action id="createByEmpi" name="新建" iconCls="create"/>  
      <action id="modify" name="查看" iconCls="update"/>  
      <action id="remove" name="删除"/>  
      <action id="print" name="打印"/> 
    </module>  
    <module id="QYFW_02" name="乡村签约服务" type="1" script="chis.application.fhr.script.PremaritalCheckFemaleForm"> 
      <properties>
        <p name="entryName">chis.application.fhr.schemas.PER_XCQYFW</p>
        <p name="isAutoScroll">true</p>
      </properties>  
      <action id="save" name="确定" group="create||update" /> 
    </module>
    <!--亿家签约链接-->
    <module id="YJQY" name="签约管理" script="chis.application.fhr.script.YjqyManageModule">
	</module>
    <!--居民签约-->
    <module id="JMQY" name="居民签约" script="chis.script.CombinedDocList">
		<properties>
			<p name="entryName">chis.application.fhr.schemas.EHR_PersonContractService</p>
			<p name="manageUnitField">c.manaUnitId</p>
			<p name="areaGridField">c.regionCode</p>
		</properties>
		<action id="list" name="列表视图" viewType="list" ref="chis.application.fhr.FHR/FHR/JMQY_01" />
	</module>
	<module id="JMQY_01" name="居民签约list" script="chis.application.fhr.script.PersonnalContractList" type="1"> 
		<properties> 
			<p name="entryName">chis.application.fhr.schemas.EHR_PersonContractService</p> 
		</properties>
		<action id="create" name="新增签约" iconCls="add"/>
		<action id="signservice" name="签约服务" iconCls="save"/>
		<action id="update" name="签约修改" />
		<action id="send" name="上传捷士达" iconCls="arrow-up"/>
		<action id="down" name="下载捷士达签约信息" iconCls="arrow-down"/>
	</module>
	<!--亿家签约统计链接 zhaojian 2017-11-08-->
    <module id="YJQYTJ" name="签约统计" script="chis.application.fhr.script.YjqytjManageModule">
	</module>
	</catagory>
</application>