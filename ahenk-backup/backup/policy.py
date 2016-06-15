#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: Seren Piri <seren.piri@agem.com.tr>

from plugins.backup.backup_util import BackupUtil

def handle_policy(profile_data, context):
    print("BACKUP PLUGIN")
    print('Data: {}'.format(str(profile_data)))
    backup = BackupUtil(profile_data, context, "policy")
    backup.backup()
    print("BACKUP PLUGIN PROCESSED")
