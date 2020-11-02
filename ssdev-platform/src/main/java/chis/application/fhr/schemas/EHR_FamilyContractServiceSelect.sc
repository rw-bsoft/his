<?xml version="1.0" encoding="UTF-8"?>
<entry entity-name="chis.application.fhr.schemas.EHR_FamilyContractServiceSelect" tableName="chis.application.fhr.schemas.EHR_FamilyContractService" alias="家庭签约服务项目">
	<item id="FS_Id" alias="主键" pkey="true" hidden="true" type="string" length="32"
		fixed="true" generator="assigned" display="2">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="32"
				startPos="1" />
		</key>
	</item>
	<item id="FC_Id" alias="家庭签约主键" type="string" length="32" hidden="true" />
	<item id="FS_EmpiId" alias="家庭成员ID" type="string" width="160"
		length="32" evalOnServer="true" hidden="true">
	</item>
	<item ref="b.personName" fixed="true"/>
	<item ref="c.signFlag" />
	<!--<item id="FS_EmpiId_text" alias="家庭成员" type="string" length="60"
		evalOnServer="true" />-->
	<item id="FS_PersonGroup" alias="人群分类" fixed="true" type="string"
		width="300" length="1000">
		<!--<dic>
				<item key="0" text="非重点人群" />
				<item key="1" text="老年人" />
				<item key="2" text="慢性病患者" />
				<item key="3" text="孕产妇" />
				<item key="4" text="0-6岁儿童" />
				<item key="5" text="残疾人群" />
				<item key="6" text="重性精神病患者" />
			</dic>-->
	</item>
	<item id="FS_Kind" alias="服务项目" type="string" fixed="true" width="495"
		length="1000">
		<!--<dic>
				<item key="1" text="1.建立更新相应健康档案" />
				<item key="2" text="2.健康评估及规划(1次/年)" />
				<item key="3" text="3.主动发放健教材料(1次/年)" />
				<item key="4" text="4.主动告知健康信息(1次/年)" />
				<item key="5" text="5.主动的分类健康咨询和指导" />
				<item key="5.1" text="5.1 主动的分类健康咨询和指导1次/年" />
				<item key="5.2" text="5.2 主动的分类健康咨询和指导2次/年" />
				<item key="5.3" text="5.3 主动的分类健康咨询和指导4次/年" />
				<item key="6" text="6.上门健康咨询和指导" />
				<item key="7" text="7.免费物理检查：身高、体重、腰围、臀围、血压" />
				<item key="8" text="8.转诊预约服务" />
				<item key="9" text="9.慢病危险因素干预" />
				<item key="10" text="10.健康教育及促进" />
				<item key="11" text="11.家庭保健员培养" />
				<item key="12" text="12.免费义诊 1次/年" />
				<item key="13" text="13.免费体检1次/年" />
				<item key="14" text="14.中医养生保健服务" />
				<item key="15" text="15.机构其他特色服务" />
			</dic>-->
	</item>
	<!--<item id="FS_Kind_other" alias="其他机构特色服务项目" display="0" type="string" fixed="true" width="495" length="1000">
			<dic id="chis.dictionary.otherServiceSelect" render="LovCombo"/>
		</item>-->
	<item id="FS_Disease" alias="疾病" type="string" fixed="true" width="495"
		length="200" hidden="true" />
	<!--<item id="FS_SexCode" alias="性别" type="string" fixed="true" width="495"
			length="200" hidden="true">
			<dic id="chis.dictionary.gender" />
		</item>
		<item id="ServiceFlag" alias="服务状态" type="string" length="1"
				display="0">
				<dic>
					<item key="1" text="签约" />
					<item key="2" text="解约" />
				</dic>
			</item>
		<dic id="serviceContent" render="ElLovCombo"/> -->
	<item id="FS_CreateDate" alias="签约日期" fixed="true" type="date" defaultValue="%server.date.date"
		width="120"/>
	<item ref="b.sexCode" fixed="true" />
	<item ref="b.birthday" fixed="true" />
	<item ref="d.FC_Sign_Flag" />
	<relations>
		<relation type="parent"
			entryName="chis.application.mpi.schemas.MPI_DemographicInfo">
			<join parent="empiId" child="FS_EmpiId" />
		</relation>
		<relation type="parent" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="empiId" child="FS_EmpiId" />
		</relation>
		<relation type="parent" entryName="chis.application.fhr.schemas.EHR_FamilyContractBase">
			<join parent="FC_Id" child="FC_Id" />
		</relation>
	</relations>
</entry>