# Minecraft parkour plugin for your server
 > Minecraft plugin to create fun parkour arenas
 
 ## Download
 
 Download jar [here](Parkour.jar) (native Minecraft 1.12.2)
 
 To setup simply put this file to your *plugins* folder.  
 You might also need [Vault](https://www.spigotmc.org/resources/vault.34315/) or an economy plugin if you have not one installed yet
 
 ## Features
 
 - You have to run without stopping to complete the parkour!
 - Bonus for completing
 - Create multiple arenas
 - Customize settings in config
 - Super simple to install and use
 
 **This project is under continous development. Any ideas and feedback is appreciated**
 
 ## Setting up
 
 
 1. **Select corners of your parkour arena**  
   
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   You can do that using */parkour setpos1* and */parkour setpos2* commands  
   
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   It will take your coordinates and write them into a config file *arena.yml*
   
 2. **Create parkour arena**  
   
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   Use command */parkour createarena {arenaName}* in order to create new arena
   
 3. **Set starting point**  
   
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   */parkour setspawn {arenaName}* allows you to set point where you will appear every time if you fail to finish  
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   It takes your coordinates and writes them to the config file *arena.yml*
   
 4. **Bonus for finishing**  
   
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 To set up bonus for completing the parkour use */parkour setreward {arenaName} {moneyAmount}*  
 
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  Also, you need to place a sign at the finish point of your parkour with the following lines  
  - [Parkour]
  - arenaName
  At the rest 2 lines you can write whatever you'd like to
  
 ## Permissions
 
The following permissions are considered
- parkour.admin - *is necessary to use any plugin commands* 
- parkour.nogift - *whoever has that permission **will NOT** be able to get the bonus for finishing
- parkour.invulnerability - *with that permission your potion effects will not be removed once you enter areba*
- parkour.signs - *allows you to create signs for parkour arenas*


 ## Demos
 
 **Running non-stop and getting the bonus of 1000$ for finishing!**
 ![Winner](success.gif)
 
 **Trying to sneak or stopping will result in automatic fail**
 ![Trying to sneak or stopping](fail.gif)
