glassbright
===========

Keep your Glass display on brighter, for longer.

I've found Google Glass power scheme to be too aggressive for me, and frequently experienced the display going off before I wanted it to.  Additionally, the device is usually dimmed so much that, unless in a very dark room, it is difficult to read.  I decided to write this tool to address this for myself, and thought I should share it with anyone else who might have had similar problems.

This is a simple implementation and almost certainly has bugs and changes that can make it better.  Please let me know if you have any requests!

TO USE: Import the project.

Commands:
"OK Glass, toggle brightness"   If off, turns on GlassBright and inserts a livecard.  If on, turns off GlassBright.

Menu
Change Timeout                  Cycles through the timeout options.  5, 10, 15, 30, never (stays on until dismissed)
Exit GlassBright                Turns off GlassBright and removes the livecard

Known Issues
- If the timeout is changed, it will not actually enact that change until you interact with or wake the device again.
- If the command "toggle brightness" is given and GlassBright livecard already exists, the command works but stays on the menu until device sleeps or user dismisses it.
- To build in Eclipse, after importing, if you see an error, you will need to:
  - Right click the project
  - Select Properties
  - In "Android" properties, set Project Build Target to "Glass Development Kit Sneak Peak" (4.0.3, API 15).  Eclipse doesn't seem to remember this setting.

Copyright 2014 Lucas Dummitt https://plus.google.com/+LucasDummitt

Portions of this code are derived or influenced from sample code from Google.  So... thanks guys!

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
