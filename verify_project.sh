#!/bin/bash
set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Starting Full Project Verification...${NC}"

# Auto-detect JAVA_HOME on Windows (Git Bash/MSYS) if not set
if [[ -z "$JAVA_HOME" && ("$OSTYPE" == "msys" || "$OSTYPE" == "cygwin") ]]; then
    echo -e "${BLUE}🔍 JAVA_HOME not set. Attempting to locate on Windows...${NC}"
    # Common path for Microsoft OpenJDK
    MS_JDK_PATH="C:\Program Files\Microsoft\jdk-17.0.17.10-hotspot"
    if [ -d "$MS_JDK_PATH" ]; then
        echo -e "${GREEN}Found Microsoft OpenJDK at: $MS_JDK_PATH${NC}"
        export JAVA_HOME="$MS_JDK_PATH"
    fi
fi

if [[ -z "$JAVA_HOME" ]]; then
    echo -e "${BLUE}ℹ️  JAVA_HOME is: (Not Set)${NC}"
else
    echo -e "${BLUE}ℹ️  JAVA_HOME is: $JAVA_HOME${NC}"
fi

echo -e "${BLUE}1️⃣  Cleaning datasets and python figures...${NC}"
rm -rf datasets/*
rm -rf python/figs/*

echo -e "${BLUE}2️⃣  Generating Ground Truth Data (Python)...${NC}"
python3 python/generate_all_data.py

echo -e "${BLUE}3️⃣  Running Java Tests...${NC}"
./gradlew clean test

echo -e "${BLUE}3️⃣  Generating Comparison Plots...${NC}"
python3 python/generate_all_plots.py
echo "Generating Accuracy Table..."
python python/generate_accuracy_plot.py

echo "Generating Comparison Table..."
python python/generate_comparison_table.py

echo -e "${GREEN}✅ All verification steps completed successfully!${NC}"
