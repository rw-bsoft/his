<entity>
  <permissions>
    <p principal="$Others" mode="15">
      <conditions>            
      </conditions>
    </p>
    <p principal="T01" mode="15">
      <conditions>            
        <filter action="query">
          ['eq',['$','a.createUser'],['$','%user.properties.refUserId']]
        </filter>
      </conditions>
    </p>
  </permissions>
</entity>