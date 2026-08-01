

Gourd Community Wallet Desktop v0.6.0 has been released (currently only supports Conflux wallets). Project address: https://github.com/acuilab/acuibc

# Download

**Download via GitHub**

| Link | Description |
| - | - |
| [gourd-v0.6.0.zip](https://github.com/acuilab/acuibc/releases/download/v0.6.0/gourd-v0.6.0.zip) | Does not bundle JRE, requires separate installation of JRE 8. Supports Windows, Linux, and macOS. Suitable for experienced users. |
| [gourd-with-jre-v0.6.0.zip](https://github.com/acuilab/acuibc/releases/download/v0.6.0/gourd-with-jre-v0.6.0.zip) | Bundles Windows version of JRE 8. Supports Windows only. Suitable for beginners. |

**Download via Baidu Netdisk**

Link: https://pan.baidu.com/s/1a67nKOo7P7AVgOthcEwe5w
Extraction code: zlqn

# Installation

This software is distributed as a compressed archive. Extract it directly to your hard drive to use. Note that due to development platform limitations, the extraction path on your hard drive must not contain Chinese characters.
If you downloaded the version without a bundled JRE 8, you need to install JRE 8 separately and set up the environment variables.

Extract to the D drive as shown below:<br />
<img src="https://b3logfile.com/file/2020/08/image-0cfd0755.png" width="50%" />

Open the `bin` directory. On Windows, double-click `gourd64.exe`. On Linux and macOS, execute the `gourd` script file.<br />
<img src="https://b3logfile.com/file/2020/08/image-8830a9a7.png" width="50%" />

Loading window appears:<br />
<img src="https://b3logfile.com/file/2020/08/image-acc60fad.png" width="50%" />

Wait a moment, and the main application window will open:<br />
<img src="https://b3logfile.com/file/2020/08/image-cf64d9f0.png" width="50%" />

# Usage

The application is a standard desktop program featuring a menu bar and a toolbar. Below them is the workspace. The left side of the workspace contains the wallet list, and the right side displays wallet details, including basic information, native coins and tokens, and transaction history.

## Menu Bar

**File - Exit**: Exits the application.<br />
**Wallet - Create Wallet**: Opens the wallet creation wizard to create a new wallet. Currently only supports Conflux wallets.<br />
**Wallet - Import Wallet**: Opens the import wizard to import a wallet via private key or mnemonic phrase. Currently only supports Conflux wallets.<br />
**Wallet - Migrate In**: Restores wallet data saved to a disk file into this application.<br />
**Wallet - Migrate Out**: Saves the application's wallet data to a disk file.<br />
The Migrate In and Migrate Out features allow you to back up and restore wallets, as well as transfer wallets between computers.<br />
**Tools - Options**: Configures application settings.<br />
Currently, there is only one setting: "Auto Update". If enabled, the app will automatically check for updates upon startup. If an update is available, it will download automatically, and the app will update to the new version after a restart. If disabled, you must manually use the "Help - Check for Updates" menu item to update. Auto update is enabled by default.<br />
**Help - Check for Updates**: Manually checks for updates.<br />
**Help - About**: Displays software version and other information.<br />

## Toolbar

![createwallet64.png](https://b3logfile.com/file/2020/08/createwallet64-482951d5.png) Create Wallet: Opens the wallet creation wizard to create a new wallet.<br />

![importwallet64.png](https://b3logfile.com/file/2020/08/importwallet64-aeb190c2.png) Import Wallet: Opens the import wizard to import a wallet via private key or mnemonic phrase.<br />

## Create Wallet

1. Set the wallet name and select the coin type.<br />
<img src="https://b3logfile.com/file/2020/08/image-58afc29e.png" width="50%" />

2. Set the wallet password.<br />
<img src="https://b3logfile.com/file/2020/08/image-683bcca6.png" width="50%" />

3. Generate mnemonic phrase.<br />
<img src="https://b3logfile.com/file/2020/08/image-90125913.png" width="50%" />

4. Confirm mnemonic phrase.<br />
<img src="https://b3logfile.com/file/2020/08/image-1b71e724.png" width="50%" />

Click the mnemonic words in the correct order. If the order is incorrect, click the corresponding word at the top to cancel it and select again.

5. Creation complete.<br />
   The newly created wallet will appear in the left wallet list.

## Import Wallet

1. Set the wallet name and select the coin type.<br />
   <img src="https://b3logfile.com/file/2020/08/image-33f6d8a8.png" width="50%" />

2. Set the wallet password.<br />
<img src="https://b3logfile.com/file/2020/08/image-c3c28e80.png" width="50%" />


3. Enter the mnemonic phrase or private key.<br />
<img src="https://b3logfile.com/file/2020/08/image-d68a5d72.png" width="50%" />


4. Import complete.<br />
   The newly imported wallet will appear in the left wallet list.

## Migrate In

1. Select the local data file.<br />
<img src="https://b3logfile.com/file/2020/08/image-556c2394.png" width="50%" />


2. Migration complete. A prompt in the bottom-right corner will instruct you to restart the application to reload the wallets.<br />
<img src="https://b3logfile.com/file/2020/08/image-12f88754.png" width="50%" />


3. After restarting, the newly migrated wallet will be loaded into the wallet list.<br />
   Note: If a wallet with the same name exists, the new wallet's name will have a timestamp suffix added. You can rename it using the "Rename" action.

## Migrate Out

1. Select the local file to save to.<br />
<img src="https://b3logfile.com/file/2020/08/image-d2614598.png" width="50%" />


2. Migration complete.<br />

## Wallet List

The wallet list window on the left displays all wallets. Each wallet entry includes the following information and actions:<br />
<img src="https://b3logfile.com/file/2020/08/image-2491e552.png" width="50%" />


1. Wallet Name: Displays the wallet name.<br />
2. Wallet Address: Displays the wallet address.<br />
3. Transfer: Opens the transfer wizard (for native coins). See the Transfer section below.<br />
4. Delete: Deletes the current wallet.<br />
5. Open Wallet Details: Displays wallet information on the right side of the workspace.<br />
6. Wallet Balance: Displays the wallet balance.<br />
7. Refresh: Refreshes the wallet balance.<br />

## Wallet Details
<img src="https://b3logfile.com/file/2020/08/image-eccd0722.png" width="50%" />


Wallet details are displayed on the right side of the workspace. If multiple wallets are opened, you can switch between them using the tabs at the top. The top section of the wallet details window contains basic information and actions:

1. Wallet Name<br />
2. Wallet Address<br />
3. Latest Transaction Hash<br />
4. Wallet Address QR Code<br />
5. Rename Action<br />
6. Change Password Action<br />
7. Export Mnemonic Action<br />
8. Export Private Key Action<br />

Below the basic info is the native coin and token information, actions, and transaction history list.
Native coins and tokens are displayed as tab labels. Currently supported: cfx, fc, cpi.

1. Transfer: Opens the transfer wizard to initiate a transfer.<br />
2. Display Balance<br />
3. Refresh: Refreshes the balance and transaction history.<br />
4. Below the transaction history, you can filter transactions by keyword and transaction type.<br />

## Transfer

Transfers for both native coins and tokens are handled via the transfer wizard.

1. Enter transfer information.<br />
<img src="https://b3logfile.com/file/2020/08/image-1a337537.png" width="50%" />

   Note: Gas fees currently only support the system default. Manually specifying gas fees is not supported.
2. Enter wallet password.<br />
<img src="https://b3logfile.com/file/2020/08/image-35ead4f0.png" width="50%" />


3. Confirm transfer information.<br />
<img src="https://b3logfile.com/file/2020/08/image-f940b03c.png" width="50%" />


4. Transfer complete.<br />
   Use the refresh action to update your balance and transaction history.
