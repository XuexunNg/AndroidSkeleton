In order in to use this skeleton on another project, there are a few places that you will need to update:

Note: In most cases, I don't see the need to edit the http package.

Config.java
- Update values under comments API-specific URL.

provider/SkeletonContract
- function interface BoneColumns. There is a interface method that defines the columns in a table.
Add more columns for more tables

- Update CONTENT_AUTHORITY value

provider/SkeletonDatabase
- You will need to need the tables here. Require a number of updates

provider/SkeletonProvider
- You will need to add more URI here and add more handler under the io package.
- Add new entry in newResponseHandler function when you add a new entry in query()
