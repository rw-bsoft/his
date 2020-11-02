<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="MPI_Extension" alias="EMPI个人基本信息">
	<item id="id" alias="ID" type="string" length="32" hidden="true" pkey="true" >
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="EMPI" type="string" length="32" hidden="true" />
	<item id="propName" alias="属性名" type="string" length="2" width="150">
		<dic id ="chis.dictionary.mpiExtension"/>
	</item> 
	<item id="propValue" alias="值" type="string" length="200" width="250"/>
</entry>