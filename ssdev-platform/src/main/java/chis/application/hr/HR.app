<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.hr.HR" name="健康档案"  type="1">
	<catagory id="HR" name="健康档案">
		<module id="B08" name="个人健康档案管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_HealthRecord</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hr.HR/HR/B081" />
		</module>
		<module id="B081" type="1" name="个人健康档案列表"
			script="chis.application.hr.script.HealthRecordList">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_HealthRecord</p>
				<p name="removeServiceId">chis.healthRecordService</p>
				<p name="saveServiceId">chis.healthRecordService</p>
				<p name="serviceAction">healthRecordSave</p>
				<p name="listServiceId">chis.publicService</p>
				<p name="listAction">queryRecordList</p>
			</properties>
			<action id="graphic" name="家族谱" iconCls="healthDoc_familyGraphic" />
			<action id="showModule" name="新建" iconCls="create" />
			<action id="modify" name="查看" iconCls="update" />
			<action id="writeOff" name="注销" iconCls="common_writeOff" />
			<action id="zlls" name="诊疗历史" iconCls="read"/>
			<action id="showBackReason" name="查看退回原因" iconCls="update" />
			<action id="print" name="打印" />
		</module>
		<module id="A04" name="待完善健康档案" script="chis.script.CombinedDocList"
			type="1">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_HealthRecord</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hr.HR/HR/A04_1" />
		</module>
		<module id="A04_1" type="1" name="个人健康档案列表"
			script="chis.application.wl.script.MyWorkHealthRecordList" tye="1">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_HealthRecord</p>
				<p name="navField">RegionCode</p>
				<p name="navDic">manageUnit</p>
				<p name="removeServiceId">chis.healthRecordService</p>
				<p name="saveServiceId">chis.healthRecordService</p>	
				<p name="serviceAction">healthRecordSave</p>
			</properties>
			<action id="modify" name="查看" iconCls="update" />
		</module>
		<module id="A07" name="待年检的慢病档案" script="chis.script.CombinedDocList"
			type="1">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_HealthRecord</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hr.HR/HR/A07_1" />
		</module>
		<module id="A07_1" name="慢病档案列表"
			script="chis.application.wl.script.MyWorkHealthCheckList" type="1">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_HealthRecord</p>
				<p name="navField">RegionCode</p>
				<p name="navDic">manageUnit</p>
				<p name="saveServiceId">chis.healthRecordService</p>
				<p name="serviceAction">healthRecordSave</p>
			</properties>
			<action id="modify" name="查看" iconCls="update" />
		</module>
		<module id="B07" type="1" name="个人健康档案表单"
			script="chis.application.hr.script.HealthRecordForm">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_HealthRecord</p>
				<p name="saveServiceId">chis.healthRecordService</p>
				<p name="loadServiceId">chis.healthRecordService</p>
				<p name="verifyServiceId">chis.healthRecordService</p>
				<p name="cancelVerifyServiceId">chis.healthRecordService</p>
				<p name="saveAction">saveHealthRecord</p>
				<p name="loadAction">loadHealthRecordData</p>
				<p name="verifyAction">verifyHealthRecord</p>
				<p name="cancelVerifyAction">cancelverifyHealthRecord</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="verify" name="审核" iconCls="update" />
			<action id="cancelVerify" name="取消审核" iconCls="update" />
		</module>
		<!--<module id="B09" type="1" name="个人生活习惯表单" script="chis.application.hr.script.LifeStyleForm">
			<properties>
				<p name="saveAction">saveLifeStyle</p>
				<p name="loadAction">loadLifeStyleData</p>
				<p name="saveServiceId">chis.healthRecordService</p>
				<p name="loadServiceId">chis.healthRecordService</p>
				<p name="entryName">chis.application.hr.schemas.EHR_LifeStyle</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
		</module>-->
		<module id="B10" type="1" name="个人既往史列表" script="chis.application.hr.script.PastHistoryList"
			icon="default">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_PastHistory</p>
				<p name="createCls">chis.application.hr.script.PastHistoryModule</p>
				<p name="updateCls">chis.application.hr.script.PastHistoryForm</p>
				<p name="saveAction">savePastHistory</p>
				<p name="saveServiceId">chis.healthRecordService</p>
				<p name="removeServiceId">chis.healthRecordService</p>
				<p name="removeAction">deletePastHistory</p>
			</properties>
			<action id="create" name="新建" iconCls="create" group="update" />
			<action id="update" name="查看"  ref="chis.application.hr.HR/HR/B1001"/>
			<action id="remove" name="删除" group="update" />
			<action id="print" name="打印" />
			<action id="refresh" name="刷新" />
		</module>
		<module id="B1001" type="1" name="个人既往史记录表单"
			script="chis.application.hr.script.PastHistoryForm" icon="default">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_PastHistory</p>
				<p name="saveAction">savePastHistory</p>
				<p name="saveServiceId">chis.healthRecordService</p>
				<p name="removeServiceId">chis.simpleRemove</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="B11" type="1" name="个人主要问题列表"
			script="chis.application.hr.script.ProblemRecordList" icon="default">
			<properties>
				<p name="createCls">chis.application.hr.script.ProblemRecordForm</p>
				<p name="updateCls">chis.application.hr.script.ProblemRecordForm</p>
				<p name="entryName">chis.application.hr.schemas.EHR_PersonProblem</p>
				<p name="removeServiceId">chis.simpleRemove</p>
			</properties>
			<action id="create" name="新建" iconCls="create" ref="chis.application.hr.HR/HR/B1101"/>
			<action id="update" name="查看" ref="chis.application.hr.HR/HR/B1101" />
			<action id="remove" name="删除"/>
			<action id="print" name="打印" />
			<action id="refresh" name="刷新" />
		</module>
		<module id="B1101" type="1" name="个人主要问题记录表单"
			script="chis.application.hr.script.ProblemRecordForm" icon="default">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_PersonProblem</p>
				<p name="saveAction">savePersonProblem</p>
				<p name="saveServiceId">chis.healthRecordService</p>
				<p name="removeServiceId">chis.simpleRemove</p>
			</properties>
			<action id="save" name="确定" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="B12" type="1" name="家族谱" script="chis.script.demographicView">
			<properties>
				<p name="entryName">DemoGraphic</p>
			</properties>
			<actions />
		</module>
		<module id="B05" name="档案注销恢复管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_HealthRecord</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hr.HR/HR/B051" />
		</module>
		<module id="B051" type="1" name="个人健康档案列表"
			script="chis.application.hr.script.HealthRecordRevertList">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_HealthRecordzs</p>
				<p name="navField">RegionCode</p>
				<p name="navDic">chis.@manageUnit</p>
				<p name="saveServiceId">chis.healthRecordService</p>
				<p name="serviceAction">setHealthRecordNormal</p>
			</properties>
			<action id="revert" name="恢复" iconCls="healthDoc_revert" />
		</module>
		<module id="B16" name="个人既往史查询" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_PastHistorySearch</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hr.HR/HR/B16-1" />
		</module>
		<module id="B16-1" type="1" name="个人既往史查询模块"
			script="chis.application.hr.script.PastHistorySearchList">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_PastHistorySearch</p>
				<p name="saveServiceId">chis.pastHistorySearchService</p>
				<p name="serviceAction">queryPastHistoryRecord</p>
			</properties>
			<action id="modify" name="查看" iconCls="update" />
		</module>
		<module id="B34" name="个人基本信息" script="chis.script.CombinedDocList"> 
        <properties> 
          <p name="entryName">chis.application.hr.schemas.EHR_HealthRecord</p> 
        </properties>  
        <action id="list" name="列表视图" viewType="list" ref="chis.application.hr.HR/HR/B341"/> 
      </module>  
      <module id="B341" type="1" name="个人健康档案列表" script="chis.application.hr.script.BasicPersonalInformationList"> 
        <properties> 
		  <!--<p name="refZlls">chis.application.hr.HR/HR/B35</p>-->
          <p name="entryName">chis.application.hr.schemas.EHR_HealthRecord</p>  
          <p name="navField">RegionCode</p>  
          <p name="navDic">chis.@manageUnit</p>  
        </properties>
        <action id="new" name="新建" iconCls="create"/>  
        <action id="xg" name="查看" iconCls="update"/>  
        <action id="writeOff" name="注销" iconCls="common_writeOff"/>  
        <action id="print" name="打印"/>
        <action id="zlls" name="诊疗历史" iconCls="read"/>
        <action id="tjb" name="体检表" iconCls="query"/>
        <action id="cross" name="开通健康网" />
      </module>  
      <module id="B34101" type="1" name="个人基本信息表" script="chis.application.hr.script.BasicPersonalInformationForm"> 
        <properties> 
          <p name="entryName">chis.application.hr.schemas.EHR_HealthRecord_JBXX</p>  
          <p name="saveAction">saveBasicPersonalInformation</p>  
           <p name="loadAction">LoadBasicPersonalInformation</p>  
          <p name="saveServiceId">chis.basicPersonalInformationService</p> 
        </properties>  
        <action id="save" name="保存"/>  
        <action id="qk" name="新建" iconCls="create"/>
        <action id="printCheck" name="打印" iconCls="print"/>
        <action id="close" name="关闭"/>
        <action id="changeIdCard" name="变更身份证" iconCls="return" />
      </module>
       <module id="B3410101"  type="1" name="网格地址" script="chis.application.hr.script.AreaGridList"> 
        <properties> 
          <p name="entryName">chis.application.hr.schemas.EHR_AreaGrid_LIST</p>  
        </properties>  
        <action id="qd" name="确定" iconCls="update"/> 
        <action id="close" name="关闭"/> 
      </module>
		<!--EHR zhaojian 2017-11-01-->
		<module id="B35" name="EHR" type="1"
			script="chis.application.hr.script.QueryZlls_EHR">
		</module>
	</catagory>
</application>