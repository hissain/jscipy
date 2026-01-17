#!/bin/bash
set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Starting Full Project Verification...${NC}"

echo -e "${BLUE}1️⃣  Generating Ground Truth Data (Python)...${NC}"
python3 python/generate_all_data.py

echo -e "${BLUE}2️⃣  Running Java Tests...${NC}"
./gradlew clean test

echo -e "${BLUE}3️⃣  Generating Comparison Plots...${NC}"
python3 python/generate_all_plots.py

echo -e "${GREEN}✅ All verification steps completed successfully!${NC}"
