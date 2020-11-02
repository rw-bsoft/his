<?xml version="1.0" encoding="UTF-8"?>
<entity>
	<permissions>
		<p principal="$Others" mode="15">
			<conditions>			 			
			</conditions>
		</p>
		<p principal="T01" mode="15">
			<conditions>			 
				<filter action="query">
					 ['eq',['$','a.manaDoctorId'],['$','%user.properties.refUserId']] 
				</filter>			
			</conditions>
		</p>
		<p principal="post" mode="15">
			<conditions>			 
				<filter action="query">
					 ['eq',['$','a.manaDoctorId'],['$','%user.properties.refUserId']] 
				</filter>			
			</conditions>
		</p>
		<p principal="T04" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
					 
				</filter>			
			</conditions>
		</p>
		<p principal="T05" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T06" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T07" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T08" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T13" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T14" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T15" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T16" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T100" mode="15">
			<conditions>			 
				<filter action="query">
					['eq',['$','a.familyDoctorId'],["$",'%user.userId']]
				</filter>			
			</conditions>
		</p>
		<p principal="T101" mode="15">
			<conditions>			 
				<filter action="query">
					['in',['$','a.familyDoctorId'],[["$",'%user.properties.fds']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T20" mode="15">
			<conditions>
				<filter action="query">
					['in',['$','a.manaDoctorId'],[["$",'%user.properties.rds']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T23" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T22" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T20" mode="15">
			<conditions>
				<filter action="query">
					['in',['$','a.manaDoctorId'],[["$",'%user.properties.rds']]]
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>