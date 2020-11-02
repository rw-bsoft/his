<?xml version="1.0" encoding="UTF-8"?>
<entry alias="残疾人训练登记">
	<item id="id" alias="主键" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.mobileNumber" display="1" queryable="true" />
	<item ref="c.regionCode" display="1" queryable="true" alias="家庭住址"/>
	<item ref="c.regionCode_text" display="0"/>
	<!-- 
		<item ref="d.lastScore" display="1" queryable="true" />
		<item ref="d.updateScore" display="1" queryable="true" />
		<item ref="d.trainEffect" display="1" queryable="true" />
		<item ref="d.healingTarget" display="1" queryable="true" />
		<item ref="d.recoverSuggestion" display="1" queryable="true" />
		-->
	
	<item id="phrId" alias="健康档案号" length="30" display="0" />
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	<item id="homeAddress" alias="家庭住址" type="string" virtual="true" colspan="2" fixed="true" display="2"/>
	<item id="contactPhone" alias="联系电话" length="20" />
	<item id="parentName" alias="家长姓名" length="20" />
	<item id="relation" alias="与残疾人关系" length="20" >
		<dic>
			<item key="1" text="丈夫" />
			<item key="2" text="妻子" />
			<item key="3" text="儿子" />
			<item key="4" text="女儿" />
			<item key="5" text="父亲" />
			<item key="6" text="母亲" />
			<item key="7" text="孙子" />
			<item key="8" text="孙女" />
			<item key="9" text="祖父" />
			<item key="10" text="祖母" />
			<item key="11" text="外祖父" />
			<item key="12" text="外祖母" />
			<item key="13" text="女婿" />
			<item key="14" text="儿媳" />
			<item key="15" text="哥哥" />
			<item key="16" text="姐姐" />
			<item key="17" text="弟弟" />
			<item key="18" text="妹妹" />
			<item key="19" text="孙女婿" />
			<item key="20" text="孙媳" />
			<item key="21" text="岳父" />
			<item key="22" text="岳母" />
			<item key="23" text="公公" />
			<item key="24" text="婆婆" />
			<item key="25" text="姐夫" />
			<item key="26" text="嫂子" />
			<item key="27" text="妹夫" />
			<item key="28" text="弟妹" />
			<item key="29" text="侄子" />
			<item key="30" text="侄女" />
			<item key="31" text="外孙" />
			<item key="32" text="外孙女" />
			<item key="33" text="朋友" />
			<item key="35" text="曾孙" />
			<item key="36" text="曾祖父" />
			<item key="37" text="曾祖母" />
			<item key="38" text="养父" />
			<item key="39" text="养母" />
			<item key="40" text="养子" />
			<item key="41" text="养女" />
			<item key="42" text="继父" />
			<item key="43" text="继母" />
			<item key="44" text="曾外孙女" />
			<item key="46" text="外甥" />
			<item key="47" text="外甥女" />
			<item key="48" text="堂(表)兄弟" />
			<item key="49" text="堂(表)姐妹" />
			<item key="98" text="其他" />
		</dic>

	</item>
	<item id="deformityType" alias="肢体残疾类别" length="20" not-null="1">
		<dic render="LovCombo">
			<item key="1" text="偏瘫" />
			<item key="2" text="截瘫" />
			<item key="3" text="脑瘫" />
			<item key="4" text="截肢" />
			<item key="5" text="儿麻后遗症" />
			<item key="6" text="骨关节疾病" />
			<item key="7" text="其他" />
		</dic>
	</item>
	<item id="withOtherDeformity" alias="是否伴有其他残疾" length="20" >
		<dic render="LovCombo">
			<item key="1" text="视力" />
			<item key="2" text="智力" />
			<item key="3" text="听力语言" />
			<item key="4" text="精神" />
		</dic>
	</item>
	<item id="deformityDate" alias="残疾时间" type="date" not-null="1" maxValue="%server.date.today"/>
	
	<item id="deformityReason" alias="残疾原因" length="100" not-null="1">
		<dic render="LovCombo">
			<item key="1" text="先天" />
			<item key="2" text="疾病" />
			<item key="3" text="意外伤害" />
			<item key="4" text="中毒" />
			<item key="5" text="其他" />
		</dic>
	</item>
	<item id="deformityPosition" alias="残疾部位" length="100" not-null="1">
		<dic render="LovCombo">
			<item key="141404" text="上肢" />
			<item key="141408" text="下肢" />
		</dic>
	</item>
	<item id="pastMedicalTreatment" alias="既往医疗情况" length="100" colspan="2" not-null="1">
		<dic render="LovCombo">
			<item key="1" text="手术" />
			<item key="2" text="药物治疗" />
			<item key="3" text="传统方法" />
			<item key="4" text="理疗" />
			<item key="5" text="康复训练" />
			<item key="6" text="使用假肢，矫形器及辅助用具" />
			<item key="7" text="其他" />
		</dic>
	</item>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20"
		queryable="true" not-null="1" update="false" defaultValue="$user.userId">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20"
		colspan="2" anchor="100%" width="180" not-null="1" fixed="true"  queryable="true"  defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree"
			parentKey="%user.manageUnit.id" rootVisible="false" />
	</item>
	<item id="explanation" alias="需要说明的情况" length="500" xtype="textarea" colspan="3" width="200"/>
	<item id="status" alias="档案状态" type="string" length="1"
		defaultValue="0" display="0" fixed="true">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
	<item id="closeFlag" alias="结案标识" type="string" length="1"
		display="0" defaultValue="n" queryable="true">
		<dic id="chis.dictionary.closeFlag" />
	</item>
	<item id="cancellationReason" alias="档案注销原因" type="string" length="1"
		queryable="true" display="0">
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
			<item key="6" text="作废" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="cancellationUser" alias="注销人" type="string" length="20"
		fixed="true" colspan="2" anchor="100%" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="cancellationDate" alias="注销日期" type="date" maxValue="%server.date.today"
		fixed="true" display="0">
	</item>
	<item id="cancellationUnit" alias="注销单位" type="string" length="20"
		fixed="true" colspan="2" anchor="100%" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="createUnit" alias="录入单位" type="string" length="20"
		update="false" fixed="true" width="165"
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入人" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime"  xtype="datefield"  update="false"
		fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" 
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
		<!-- 
			<relation type="children" entryName="DEF_LimbTerminalEvaluate">
				<join parent="id" child="defId" />
			</relation>
			-->
	</relations>
</entry>
