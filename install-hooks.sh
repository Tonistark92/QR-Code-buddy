#!/bin/bash

echo "Installing Git hooks..."

# Copy hooks to .git/hooks/
cp hooks/pre-commit .git/hooks/pre-commit
cp hooks/pre-push .git/hooks/pre-push

# Make hooks executable
chmod +x .git/hooks/pre-commit
chmod +x .git/hooks/pre-push

echo "✅ Git hooks installed successfully!"
echo "Run './install-hooks.sh' for new team members to set up hooks."
