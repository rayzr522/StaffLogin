# StaffLogin
Forces staff to have to log in before they can move or do anything. Staff members are determined as any users with the `StaffLogin.require` permission. In addition, all the commands require `StaffLogin.require`.

Players are logged _out_ either when they disconnect from the server or when they do `/logout`. The following things are prevented while a staff member is logged out:
- Movement
- Block breaking
- Block placing
- Interaction (left-click, right-click, pressure-plates, etc.)
- Bucket use
- Dropping items
- Clicking inventory slots (buggy in creative-mode, blame Mojang)
- Crafting items
- Executing commands (other than `/login`, `logout`, and `/password`)

Some of them might seem like overkill, considering it _should_ be impossible to craft items if you can't click your inventory slots, and you can't use a bucket if you can't interact. This is probably true, but it doesn't hurt to be too safe. Who knows what odd stuff might happen with hacked-clients and such :D

_This plugin was done as a request for the **HydrusPvP** server._

#### [Downloads](https://github.com/Rayzr522/Sta/releases)

## Commands
### `/login`
Permission: `StaffLogin.require`  
Usage: `/login <password>`  
Description: Logs you in if you have set up a password.

### `/logout`
Permission: `StaffLogin.require`  
Usage: `/logout`  
Description: Logs you out.

### `/password`
Permission: `StaffLogin.require`  
Usage: `/password <password>`
Description: Sets your password. If you already have a password, it requires you to be logged in to change it.