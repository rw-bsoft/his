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
					['eq',['$','a.createUser'],["$",'%user.properties.refUserId']]
					 
				</filter>	
			</conditions>
		</p>
		<p principal="post" mode="15">
			<conditions>
				<filter action="query">
					['eq',['$','a.createUser'],["$",'%user.properties.refUserId']]
					 
				</filter>	
			</conditions>
		</p>
		<p principal="T100" mode="15">
			<conditions>
				<filter action="query">
					['eq',['$','a.createUser'],["$",'%user.properties.refUserId']]
					 
				</filter>	
			</conditions>
		</p>
		<p principal="T04" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.createUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T05" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.createUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T06" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.createUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T11" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.createUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T12" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.createUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T13" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.createUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T14" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.createUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T20" mode="15">
			<conditions>		 
				<filter action="query">
					['in',['$','a.createUser'],[["$",'%user.properties.rds']]]
				</filter>			
			</conditions>
		</p>		
	</permissions>
</entity>