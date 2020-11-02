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
					['eq',['$','b.manaDoctorId'],['$','%user.properties.refUserId']]
				</filter>			
			</conditions>
		</p>
		<p principal="post" mode="15">
			<conditions>			 
				<filter action="query">
					['eq',['$','b.manaDoctorId'],['$','%user.properties.refUserId']]
				</filter>			
			</conditions>
		</p>
		<p principal="T05" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','b.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>

		<p principal="T07" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','b.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T08" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','b.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T20" mode="15">
			<conditions>
				<filter action="query">
					['in',['$','b.manaDoctorId'],[["$",'%user.properties.rds']]]
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>