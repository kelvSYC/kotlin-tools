@AGENTS.md

## Claude Setup

Before using Claude Code in this repo, create `.claude/settings.local.json` (gitignored):
```json
{
  "permissions": {
    "allow": [
      "Skill(update-config)",
      "Skill(update-config:*)"
    ]
  }
}
```
