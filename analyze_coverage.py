import xml.etree.ElementTree as ET
import os

report_path = 'build/reports/jacoco/test/jacocoTestReport.xml'

if not os.path.exists(report_path):
    print("Report file not found!")
    exit(1)

root = ET.parse(report_path).getroot()

classes_data = []

packages = list(root.iter('package'))

for p in packages:
    package_name = p.get('name')
    for c in p.iter('class'):
        class_name = c.get('name')
        full_name = f"{package_name}.{class_name}"
        
        # Find instruction counter
        instruction_counter = None
        for cnt in c.iter('counter'):
            if cnt.get('type') == 'INSTRUCTION':
                instruction_counter = cnt
                pass 
        
        # Try to find class-level counter direct child
        for cnt in c.findall('counter'):
             if cnt.get('type') == 'INSTRUCTION':
                 instruction_counter = cnt
                 break
        
        if instruction_counter:
            missed = int(instruction_counter.get('missed'))
            covered = int(instruction_counter.get('covered'))
            total = missed + covered
            if total > 0:
                coverage = (covered / total) * 100
                classes_data.append((full_name, coverage, missed, total))
        else:
             # Aggregate from methods
             missed_sum = 0
             covered_sum = 0
             found_method_counter = False
             for m in c.iter('method'):
                 for cnt in m.findall('counter'):
                     if cnt.get('type') == 'INSTRUCTION':
                         missed_sum += int(cnt.get('missed'))
                         covered_sum += int(cnt.get('covered'))
                         found_method_counter = True
             
             if found_method_counter:
                 total = missed_sum + covered_sum
                 if total > 0:
                     coverage = (covered_sum / total) * 100
                     classes_data.append((full_name, coverage, missed_sum, total))

# Sort by coverage (ascending) for the detailed report
classes_data.sort(key=lambda x: x[1])

with open('coverage_report.txt', 'w') as f:
    f.write(f"Found {len(classes_data)} classes.\n")
    f.write(f"{'Class':<60} {'Cov%':<10} {'Missed/Total'}\n")
    f.write('-'*90 + '\n')
    for name, coverage, missed, total in classes_data:
        if coverage < 90:
            f.write(f"{name:<60} {coverage:6.2f}%    {missed}/{total}\n")
            print(f"{name:<60} {coverage:6.2f}%    {missed}/{total}")

    # Calculate total coverage from root counters
    total_missed = 0
    total_covered = 0
    for cnt in root.findall('counter'):
        if cnt.get('type') == 'INSTRUCTION':
            total_missed = int(cnt.get('missed'))
            total_covered = int(cnt.get('covered'))
            break

    if (total_missed + total_covered) > 0:
        total_instructions = total_missed + total_covered
        total_coverage = (total_covered / total_instructions) * 100
        f.write(f"\nTotal Project Coverage: {total_coverage:.2f}% ({total_covered}/{total_instructions})")
        
        print("\nTop 10 Classes by Missed Instructions:")
        print(f"{'Class':<60} {'Missed':<10} {'Total':<10} {'Coverage':<10}")
        print("-" * 90)
        
        # Sort by missed instructions descending
        sorted_by_missed = sorted(classes_data, key=lambda x: x[2], reverse=True)
        
        for name, coverage, missed, total in sorted_by_missed[:10]:
             print(f"{name:<60} {missed:<10} {total:<10} {coverage:.2f}%")

        print(f"\nTotal Project Coverage: {total_coverage:.2f}% ({total_covered}/{total_instructions})")
