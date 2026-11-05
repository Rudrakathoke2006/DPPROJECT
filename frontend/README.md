# JSP frontend

Use the root README for the complete launch, reset and test instructions.

From this folder the compatible wrapper is:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1
```

Its default runtime is `frontend/runtime`; relative `-RuntimeDirectory` arguments to this wrapper are relative to `frontend`. The root launcher and reset default to that same directory. The root launcher also supports `-Console`.
