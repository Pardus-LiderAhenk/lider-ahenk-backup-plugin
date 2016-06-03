#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author:
from base.plugin.abstract_plugin import AbstractPlugin
import json
import pexpect
from  base.model.enum.ContentType import ContentType

class Backup(AbstractPlugin):
    def __init__(self, data, context):
        super(AbstractPlugin, self).__init__()
        print('init backup...')
        self.data = data
        self.context = context
        self.logger = self.get_logger()
        self.message_code=self.get_message_code()

    def handle_policy(self):
        print('Handling policy...')
        print(str(json.loads(self.data)))
        self.logger.debug("Starting to backup... Reading backup profile json")
        backupProfile = json.loads(self.data)
        self.logger.debug("Successfully readed backup profile json.") 
        destinationPath = str(backupProfile['username']) + '@' + str(backupProfile['destHost']) + ':' + str(
            backupProfile['destPath'])
        self.logger.debug("Destination path ==> " + str(destinationPath))
        for source in backupProfile['directories']:
            self.logger.debug("Trying to backup for source ==> " + str(source['sourcePath']))
            options = ''
            path = source['sourcePath'] + ' ' + destinationPath
            command = ''

            if backupProfile['useLvmShadow']:
                logicalVolumeSize = str(source['logicalVolumeSize'])
                logicalVolume = str(source['logicalVolume'])
                virtualGroup = str(source['virtualGroup'])
                create_lv_command = 'lvcreate -L ' + logicalVolumeSize + ' -s -n ' + logicalVolume + ' ' + virtualGroup
                (result_code, p_out, p_err) = self.execute_command(create_lv_command, shell=True)
                if (result_code == 0):
                    self.logger.debug('Logical volume created successfully. LV ==>' + str(logicalVolume))
                    (result_code, p_out, p_err) = self.execute_command('mkdir -p ' + source['sourcePath'], shell=True)
                    (result_code, p_out, p_err) = self.execute_command(
                        'mount ' + logicalVolume + ' ' + source['sourcePath'], shell=True)
                    self.logger.debug('Mount path created successfully. Mount path ==>' + source['sourcePath'])

            if source['recursive']:
                options = options + ' -r '
            if source['preserveGroup']:
                options = options + ' -g '
            if source['preserveOwner']:
                options = options + ' -o '
            if source['preservePermissions']:
                options = options + ' -p '
            if source['archive']:
                options = options + ' -a '
            if source['compress']:
                options = options + ' -z '
            if source['existingOnly']:
                options = options + ' --existing '
            if source['excludePattern']:
                options = options + ' --exclude "' + source['excludePattern'] + '" '
            result_code = -1
            if (backupProfile['useSsh']):
                sshOptions = ' ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null --progress -p ' + str(
                    backupProfile['destPort'])
                command = 'rsync ' + options + ' -e "' + sshOptions + '" ' + path
                self.logger.debug("Command ==> " + command)
                (result_code, p_out, p_err) = self.execute_command(command, shell=True)
            else:
                sshOptions = ' ssh -q -oStrictHostKeyChecking=no -oUserKnownHostsFile=/dev/null -oPubkeyAuthentication=no -p ' + str(
                    backupProfile['destPort'])
                command = 'rsync ' + options + ' -e "' + sshOptions + '" ' + path
                self.logger.debug("Command ==> " + command)
                result_code = self.runCommandWithPassword(command, backupProfile['password'])
            if result_code == 0:
                self.logger.debug("Sync is successfull for source ==> " + str(source['sourcePath']))
            else:
                self.logger.debug("Sync is unsuccessfull for source ==> " + str(source['sourcePath']))

            tmp={}
            tmp['oner']='unal'
            self.context.create_response(code=self.message_code.POLICY_PROCESSED.value,message='ali ozkan',data=json.dumps(tmp),content_type=ContentType.APPLICATION_JSON.value)

    def runCommandWithPassword(self, command, password, timeout=30):
        child = pexpect.spawn(command, timeout=timeout)
        child.expect(['password: '])
        child.sendline(password)
        child.expect(pexpect.EOF)
        child.close()
        if 0 != child.exitstatus:
            return -1
        else:
            return 0


def handle_policy(profile_data, context):
    print("This is backup plugin")
    print('Data: {}'.format(str(profile_data)))
    backup = Backup(profile_data, context)
    backup.handle_policy()
