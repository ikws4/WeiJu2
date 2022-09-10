<h1 align="center">
  <img src="https://user-images.githubusercontent.com/47056144/184565081-fab04563-4590-4f3c-8f86-12d2dd429f8a.png" width="40%" alt="WeiJu2" />
</h1>

The first scriptable xposed module, provides a new way to change the application behavior.

Powered by Lua and made with ♥


# Features

- Lua scripting
- Simple and intuitive hook API
- Share your package with others by publish it at [WeiJu2-Scripts](https://github.com/ikws4/WeiJu2-Scripts)

# Q&A

### How to write a hook?

```lua
-- You can import any java class
local Toast = import("android.widget.Toast")
local Activity = import("android.app.Activity")
local Bundle = import("android.os.Bundle")
local StringBuilder = import("java.lang.StringBuilder")

-- Hook a method
hook {
  class = Activity,
  returns = void,
  method = "onCreate",
  params = {
    Bundle
  },
  after = function(this, params)
    -- This will call the `StringBuilder(CharSequence seq)` constructor
    -- to instantiate a StringBuilder object
    local sb = StringBuilder("Hello, ")
    sb:append("WeiJu2")
  
    Toast:makeText(this, sb:toString(), Toast.LENGTH_SHORT):show()
    --              ^
    -- Note: `this` is the Activity instance
  end,
}

-- Hook a constructor
local View = import("android.view.View")
local Context = import("android.content.Context")
local AttributeSet = import("android.util.AttributeSet")

hook {
  class = View,
  params = {
    Context,
    AttributeSet,
    int
  },
  after = function(this, params)
  
  end,
}
```


### How to modify class fields?

```lua
-- With the `import` function you can bind any java class, and access all the fields and methods that defined
-- in that class. No more `XposedHelper.setStaticObjectField(Build.class, "DEVICE", "coral")` much cleaner!
local Build = import("android.os.Build")

Build.DEVICE = "coral"
Build.PRODUCT = "coral"
Build.MODEL = "Google Pixel 4XL"
Build.BRAND = "google"
Build.MANUFACTURER = "google"
Build.VERSION.RELEASE = "13"
```


### How to import a package?

```lua
require("ikws4.system_variable").setup {
  -- config goes here
}
```


### How to create a package?

A basic package template:

```lua
--[=[ 
@metadata
  return {
    name = "your_package",
    author = "you",
    version = "1.0.0",
    description = "Describle your package",
    example = [[
      -- you can provide an example here for others to reference
    ]]
  }
@end
--]=]

local config = {
}

local M = {}

M.setup = function(opts)
  config = table.extend(config, opts or {})
  
  -- write hook here
end

return M
```

Want to share your work with others? Create a PR at [WeiJu2-Scripts](https://github.com/ikws4/WeiJu2-Scripts).


# API

### import
```lua
--- Examples:
---   local StringBuilder = import("java.lang.StringBuilder")
---   local my_string_builder = StringBuilder("hi")
---   print(my_string_builder:toString())
---
--- @param class_name string
--- @return Class
function import(class_name) end
```

### object
```lua
--- Examples:
---   local Runnable = import("java.lang.Runnable")
---   local my_runnable = object(Runnable, {
---     run = function(this)
---     
---     end
---   })
---   my_runnable:run()
---
--- @param class Class
--- @param proxy table<string, function>
--- @return Class
function object(class, proxy) end
```

### hook
```lua
--- Exampels:
---   local View = import("android.view.View")
---   local Context = import("android.content.Context")
---   local AttributeSet = import("android.util.AttributeSet")
---
---   hook {
---     class = View,
---     params = {
---       Context,
---       AttributeSet,
---       int
---     },
---     after = function(this, params)
---       -- Add your own logic after constructor is called
---        
---     end
---   }
---
---   local Canvas = import("android.graphics.Canvas")
---
---   hook {
---     class = View,
---     returns = void,
---     method = "onDraw",
---     params = {
---       Canvas
---     },
---     after = function(this, params)
---       local canvas = params[1]
---       -- Using canvas to draw anything you want
---     
---     end
---   }
---
--- @param config table This table accepts the following keys
---                     - class: (Class) The hook target.
---                     - returns: (nil or Class) The method return type.
---                     - method: (string) The method name.
---                     - params: (nil|table) The method argument types, can be nil if there is no argument.
---                     - before: (nil|function) Executed before the method is called.
---                     - after: (nil|function) Executed after the method is called.
---                     - replace: (nil|function) A simple version of `before`, use to rewrite the whole method.
--- @return Unhook
function hook(config) end
```

# Screenshots

<img src="https://user-images.githubusercontent.com/47056144/183251553-9dce66f7-953c-45b9-b741-0ae8e0b567af.png" />
