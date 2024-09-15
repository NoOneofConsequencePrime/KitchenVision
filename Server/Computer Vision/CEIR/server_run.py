import subprocess
import shutil


if __name__ == "__main__":
    image_path = "C:/xampp/htdocs/uploads/temp.jpg"
    shutil.copy(image_path, "C:/Users/john.qing/PycharmProjects/CEIRTest/images/temp.jpg")
    command = f"python exe.py --image temp"
    subprocess.run(command, check=True, shell=True, text=True)
