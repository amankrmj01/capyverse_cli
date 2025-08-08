[Setup]
AppName=CapyVerse
AppVersion=1.0.0
AppPublisher=amankrmj
AppPublisherURL=https://github.com/amankrmj01
DefaultDirName={autopf}\CapyVerse
DefaultGroupName=CapyVerse
OutputDir=../installer
OutputBaseFilename=CapyVerse-Native-Setup-1.0.0
Compression=lzma
SolidCompression=yes
WizardStyle=modern
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
ChangesEnvironment=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked
Name: "addtopath"; Description: "Add CapyVerse to user PATH"; GroupDescription: "System Integration"; Flags: checkedonce

[Files]
; Native executable only - no JVM required!
Source: "../build/native/nativeCompile/capy.exe"; DestDir: "{app}\bin"; Flags: ignoreversion
Source: "../installer/app-icon.ico"; DestDir: "{app}"; Flags: ignoreversion
Source: "../installer/LICENSE"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\CapyVerse"; Filename: "{app}\bin\capy.exe"; WorkingDir: "{app}"; IconFilename: "{app}\app-icon.ico"
Name: "{group}\{cm:UninstallProgram,CapyVerse}"; Filename: "{uninstallexe}"
Name: "{autodesktop}\CapyVerse"; Filename: "{app}\bin\capy.exe"; WorkingDir: "{app}"; IconFilename: "{app}\app-icon.ico"; Tasks: desktopicon

[Registry]
Root: HKCU; Subkey: "Environment"; ValueType: expandsz; ValueName: "Path"; ValueData: "{olddata};{app}\bin"; Tasks: addtopath; Check: NeedsAddPath('{app}\bin')

[Run]
Filename: "{app}\bin\capy.exe"; Parameters: "--help"; Description: "Test CapyVerse installation"; Flags: waituntilterminated postinstall skipifsilent; WorkingDir: "{app}"

[Code]
function NeedsAddPath(Param: string): boolean;
var
  OrigPath: string;
begin
  if not RegQueryStringValue(HKEY_CURRENT_USER,
    'Environment',
    'Path', OrigPath)
  then begin
    Result := True;
    exit;
  end;
  Result := Pos(';' + Param + ';', ';' + OrigPath + ';') = 0;
end;
