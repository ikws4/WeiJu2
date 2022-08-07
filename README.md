# WeiJu2 (Working In Progress)

Scriptable Xposed Module


# Features

- Lua Scripting
- Provide a scripts/package [repository](https://github.com/ikws4/WeiJu2-Scripts)

# How to write a hook?

```lua
-- You can import any java class
local Toast = import("android.widget.Toast")
local Activity = import("android.app.Activity")
local Bundle = import("android.os.Bundle")

hook {
  class = Activity,
  returns = void,
  method = "onCreate",
  params = {
    Bundle
  },
  after = function(this, params)
    Toast:makeText(this, config.toast_msg, Toast.LENGTH_SHORT):show()
    --              ^
    -- Note: `this` is the Activity instance
  end,
}
```

# Screenshots

<img src="https://user-images.githubusercontent.com/47056144/183251553-9dce66f7-953c-45b9-b741-0ae8e0b567af.png" />
