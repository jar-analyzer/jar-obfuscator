import os
import shutil
import sys
import subprocess


def copy_dll_files(source_dir, target_dir):
    os.makedirs(target_dir, exist_ok=True)
    for root, dirs, files in os.walk(source_dir):
        for file in files:
            if file.endswith(".dll"):
                source_path = os.path.join(root, file)
                target_file = file
                if not file.startswith("lib"):
                    target_file = "lib" + file
                target_path = os.path.join(target_dir, target_file)
                shutil.copy(source_path, target_path)
                print(f"copy: {source_path} -> {target_path}")


def copy_so_files(source_dir, target_dir):
    os.makedirs(target_dir, exist_ok=True)
    for root, dirs, files in os.walk(source_dir):
        for file in files:
            if file.endswith(".so"):
                source_path = os.path.join(root, file)
                target_path = os.path.join(target_dir, file)
                shutil.copy(source_path, target_path)
                print(f"copy: {source_path} -> {target_path}")


def process_so_files(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".so"):
                filepath = os.path.join(root, file)
                command = ["execstack", "-c", filepath]
                subprocess.run(command, check=True)
                print("execstack: " + filepath)


# DO NOT USE DEBUG
source_directory = "build-release"
c_source_directory = "cmake-build-release"

target_directory = "target"

if sys.platform.startswith('win'):
    copy_dll_files(source_directory, target_directory)
    copy_dll_files(c_source_directory, target_directory)
else:
    copy_so_files(source_directory, target_directory)
    process_so_files(target_directory)
