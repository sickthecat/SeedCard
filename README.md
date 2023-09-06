# SeedCard
RSA storage of seedphrase on JCOP card -- This is only a PoC until someone wants to help me make this better or fund me so i can afford to pay better programmers.
This cat is not responsible for your fuckups.
Always use in an air-gapped env.
There are many considerations when dealing with cryptography, secure storage, and smart cards, so this example should be seen as a proof of concept rather than production-ready code.

### Prerequisites
1. **Smart Card Reader**: Make sure you have a smart card reader connected to your computer.
2. **Java Card**: Insert the Java Card into the smart card reader.
3. **Java Card Development Kit**: Download and install the Java Card Development Kit suitable for your card.
4. **GlobalPlatformPro**: Download and install GlobalPlatformPro, a tool that allows you to interact with Java Card. You can get it [from GitHub](https://github.com/martinpaljak/GlobalPlatformPro).

### Steps

1. **Compile the Applet**

    - Open a terminal or command prompt in the directory where your Java Card applet code resides.
    - Use the Java Card Development Kit to compile your Java applet into a `.class` file. 
    - Convert the `.class` files into a `.cap` (Converted Applet) file, which is the file that you'll actually load onto the card. You can do this using the `converter` tool from the Java Card Development Kit.

2. **List the Card Readers**

    - Run `gp -l` to list available smart card readers and ensure that your reader and card are correctly detected.

3. **Unlock the Card (if necessary)**

    - Cards may come locked and might require you to use an authentication key to unlock them for loading applets.
    - To unlock, you might run a command like: `gp -unlock -key <key>`, where `<key>` is your specific authentication key.

4. **Install the Applet**

    - Use the `gp` tool to install the `.cap` file onto the Java Card: 
      ```
      gp -install <Path_to_your_applet.cap>
      ```
    - Replace `<Path_to_your_applet.cap>` with the actual path to your `.cap` file.

5. **Verify Installation**

    - Use `gp -list` to list all applets on the card and verify that your applet has been correctly installed.

6. **Optional: Set Applet as Default**

    - Some cards allow setting an applet as the default to run when the card is accessed. Check your card's documentation for how to do this, if desired.

7. **Test the Applet**

    - You can now interact with your applet using a smart card terminal software or your own code.

8. **Debug (if necessary)**

    - If you encounter any issues, use the `--debug` or `-d` flag with the `gp` commands to get more detailed output that can help you troubleshoot any issues.

